/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2022 Stefano Fornari. Licensed under the
 * EUPL-1.2 or later (see LICENSE).
 *
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Stefano Fornari.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * STEFANO FORNARI MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. STEFANO FORNARI SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package ste.w3.easywallet.ui.table;

import io.github.palexdev.materialfx.collections.TransformableListWrapper;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterPane;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableColumn.MFXTableColumnEvent;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.dialogs.MFXDialogs;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.SortState;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.ui.EasyWalletMain;

/**
 *
 */
public class EasyWalletTableViewSkin<T> extends SkinBase<MFXTableView<T>> {
    //================================================================================
    // Properties
    //================================================================================

    protected final VBox container;
    protected final HBox columnsContainer;
    protected final SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow;
    protected final StackPane footer;

    private final MFXFilterPane<T> filterPane;
    private final MFXStageDialog filterDialog;
    private MFXTableColumn<T> sortedColumn;

    private final MFXPagination pagination;
    private boolean init = false;

    //================================================================================
    // Constructors
    //================================================================================
    public EasyWalletTableViewSkin(MFXPaginatedTableView<T> tableView, SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow) {
        super(tableView);
        this.rowsFlow = rowsFlow;

        columnsContainer = new HBox();
        columnsContainer.getStyleClass().add("columns-container");
        Bindings.bindContent(columnsContainer.getChildren(), tableView.getTableColumns());

        filterPane = new MFXFilterPane<>();
        Bindings.bindContent(filterPane.getFilters(), tableView.getFilters());

        footer = buildFooter();

        container = new VBox(columnsContainer, rowsFlow);
        if (tableView.isFooterVisible()) {
            container.getChildren().add(footer);
        }

        filterDialog = MFXDialogs.filter(filterPane)
                .setShowMinimize(false)
                .setShowClose(false)
                .setShowAlwaysOnTop(false)
                .toStageDialogBuilder()
                .setDraggable(true)
                .setOwnerNode(container)
                .setCenterInOwnerNode(true)
                .initOwner(tableView.getScene().getWindow())
                .initModality(Modality.APPLICATION_MODAL)
                .get();
        filterDialog.setOnShown(event -> filterDialog.toFront());

        MFXGenericDialog dialog = (MFXGenericDialog)filterDialog.getContent();

        dialog.getStylesheets().add(
          EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm()
        );

        MFXButton applyButton =  new MFXButton(Labels.LABEL_BUTTON_APPLY);
        applyButton.getStyleClass().add("primary-button");

        MFXButton refreshButton =  new MFXButton(Labels.LABEL_BUTTON_RESET);
        dialog.addActions(
            Map.entry(refreshButton, e -> {
                filterPane.getOnReset().handle(e);
            }),
            Map.entry(applyButton, e -> {
                filterPane.getOnFilter().handle(e);
            })
        );

        dialog.onKeyTypedProperty().setValue((event) -> {
            if (event.getCharacter().charAt(0) == 27) {  // ESC
                filterDialog.close();
            }
        });

        getChildren().setAll(container);

        rowsFlow.setMinHeight(Region.USE_PREF_SIZE);
        rowsFlow.setMaxHeight(Region.USE_PREF_SIZE);

        pagination = new MFXPagination();
        pagination.pagesToShowProperty().bind(tableView.pagesToShowProperty());
        pagination.maxPageProperty().bind(tableView.maxPageProperty());
        pagination.setCurrentPage(tableView.getCurrentPage());
        tableView.currentPageProperty().bindBidirectional(pagination.currentPageProperty());

        container.getChildren().remove(footer);
        container.getChildren().add(buildFooter());

        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================
    /**
     * Specifies the behavior for the following changes/events:
     * <p>
     * - Handles the focus on MOUSE_PRESSED
     * <p>
     * - Handles the sorting on {@link MFXTableColumnEvent#SORTING_EVENT}
     * <p>
     * - Re-builds the cell when columns change
     * <p>
     * - Handles the footer visibility
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        MFXPaginatedTableView<T> tableView = (MFXPaginatedTableView<T>) getSkinnable();

        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> tableView.requestFocus());
        tableView.addEventFilter(MFXTableColumnEvent.SORTING_EVENT, event -> {
            TransformableListWrapper<T> transformableList = tableView.getTransformableList();
            MFXTableColumn<T> column = event.getColumn();
            if (sortedColumn != null && sortedColumn != column) {
                sortedColumn.setSortState(SortState.UNSORTED);
            }
            switch (event.getSortState()) {
                case UNSORTED: {
                    transformableList.setComparator(null, false);
                    break;
                }
                case ASCENDING: {
                    transformableList.setComparator(event.getComparator(), false);
                    break;
                }
                case DESCENDING: {
                    transformableList.setComparator(event.getComparator(), true);
                    break;
                }
            }
            sortedColumn = column;
        });

        tableView.getTableColumns().addListener((InvalidationListener) invalidated -> {
            for (MFXTableRow<T> row : rowsFlow.getCells().values()) {
                row.buildCells();
            }
        });

        tableView.footerVisibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                container.getChildren().add(footer);
            } else {
                container.getChildren().remove(footer);
            }
        });

        tableView.virtualFlowInitializedProperty().addListener((observable, oldValue, newValue) -> {
            if (!init && newValue) {
                rowsFlow.prefHeightProperty().bind(Bindings.createDoubleBinding(
                        () -> tableView.getRowsPerPage() * rowsFlow.getCellHeight(),
                        tableView.rowsPerPageProperty()
                ));

                int current = tableView.getCurrentPage();
                if (current != 1) {
                    PauseBuilder.build()
                            .setDuration(20)
                            .setOnFinished(event -> tableView.goToPage(current))
                            .getAnimation().play();
                }

                init = true;
            }
        });
    }

    /**
     * Responsible for building the table's footer.
     */
    protected StackPane buildFooter() {
        MFXTableView<T> tableView = getSkinnable();

        MFXIconWrapper filterIcon = new MFXIconWrapper("mfx-filter-alt", 16, 32).defaultRippleGeneratorBehavior();
        MFXIconWrapper clearFilterIcon = new MFXIconWrapper("mfx-filter-alt-clear", 16, 32).defaultRippleGeneratorBehavior();

        NodeUtils.makeRegionCircular(filterIcon);
        NodeUtils.makeRegionCircular(clearFilterIcon);

        filterIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            filterDialog.showDialog();
        });
        clearFilterIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            filterPane.getActiveFilters().clear();
            tableView.getTransformableList().setPredicate(null);
        });

        filterPane.setOnFilter(event -> {
            tableView.getTransformableList().setPredicate(filterPane.filter());
            filterDialog.close();
        });
        filterPane.setOnReset(event -> filterPane.getActiveFilters().clear());

        HBox container = new HBox(10, filterIcon, clearFilterIcon);
        StackPane.setAlignment(container, Pos.CENTER_LEFT);

        StackPane footer = new StackPane(container);
        footer.getStyleClass().add("default-footer");

        if (pagination != null) {
            footer.getChildren().add(pagination);
            StackPane.setAlignment(pagination, Pos.CENTER);
        }
        return footer;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double footerWidth = leftInset + footer.prefWidth(-1) + pagination.prefWidth(-1) * 2 + 10 + rightInset;
        return Math.max(footerWidth, super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset));
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(-1);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(-1);
    }
}
