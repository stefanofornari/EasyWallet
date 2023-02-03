package ste.w3.easywallet.ui;

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.mfxcore.controls.MFXIconWrapper;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.RegionUtils;
import io.github.palexdev.virtualizedfx.cell.TableCell;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.enums.ColumnsLayoutMode;
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums;
import io.github.palexdev.virtualizedfx.table.TableColumn;
import io.github.palexdev.virtualizedfx.table.TableRow;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableRow;
import io.github.palexdev.virtualizedfx.table.defaults.SimpleTableCell;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import ste.w3.easywallet.Transaction;

import ste.w3.easywallet.ledger.LedgerSource;
import ste.w3.easywallet.ledger.Order;
import ste.w3.easywallet.ledger.TableSourceSorting;

@SuppressWarnings("unchecked")
public class LedgerController extends EasyWalletDialogController implements Initializable {

    @FXML
    protected StackPane ledgerDialogPane;

    @FXML
    protected StackPane ledgerPane;

    protected VirtualTable table;
    protected final ExecutorService background = Executors.newSingleThreadExecutor();

    protected LedgerSource source = null;

    public LedgerController(final MFXGenericDialog dialog, final LedgerSource source) {
        super(dialog);
        this.source = source;
    }

    public LedgerController(final MFXGenericDialog dialog) {
        this(dialog, new LedgerSource());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Table Initialization
        table = new VirtualTable<>();
        table.setColumnsLayoutMode(ColumnsLayoutMode.VARIABLE);

        // Columns Initialization
        DefaultTableColumn<Transaction, TableCell<Transaction>> when = createColumn(table, "when", Transaction::when);
        DefaultTableColumn<Transaction, TableCell<Transaction>> amount = createColumn(table, "amount", Transaction::amount);
        DefaultTableColumn<Transaction, TableCell<Transaction>> coin = createColumn(table, "coin", Transaction::coin);
        DefaultTableColumn<Transaction, TableCell<Transaction>> hash = createColumn(table, "hash", Transaction::hash);

        table.getColumns().setAll(when, coin, amount, hash);

        // Init Content Pane
        VirtualScrollPane vsp = table.wrap();
        vsp.setLayoutMode(ScrollPaneEnums.LayoutMode.COMPACT);
        vsp.setAutoHideBars(true);

        Runnable speedAction = () -> {
            double ch = table.getCellHeight();
            double cw = table.getColumnSize().getWidth();
            VSPUtils.setVSpeed(vsp, ch / 3, ch / 2, ch / 2);
            VSPUtils.setHSpeed(vsp, cw / 3, cw / 2, cw / 2);
        };
        When.onInvalidated(table.cellHeightProperty())
                .then(i -> speedAction.run())
                .executeNow()
                .listen();
        When.onInvalidated(table.columnSizeProperty())
                .then(i -> speedAction.run())
                .executeNow()
                .listen();

        ledgerPane.getChildren().add(vsp);

        table.onScrollProperty().set((event) -> {
            //
            // do not fetch only when scrolling up (delta > 0)
            //
            if ((event.getDeltaY() < 0) && (table.getLastRowsRange().getMax().longValue() == table.getItems().size() - 1)) {
                fetch();
            }
        });

        source.page.addListener((InvalidationListener)(Observable o) -> {
            Platform.runLater(() -> {
                table.getItems().clear();
                table.getItems().addAll(source.page);
                table.scrollToRow((int)(source.page.size()-source.pageSize()));
            });
        });
    }

    private <E> DefaultTableColumn<Transaction, TableCell<Transaction>> createColumn(VirtualTable<Transaction> table, String name, Function<Transaction, E> extractor) {
        DefaultTableColumn<Transaction, TableCell<Transaction>> column = new DefaultTableColumn<>(table, name);
        column.setCellFactory(u -> new AltCell<>(u, extractor));

        MFXIconWrapper sortIcon = new MFXIconWrapper();
        sortIcon.setIcon(SortingIcons.NONE.newIcon());
        sortIcon.getStyleClass().add("icon-button");
        sortIcon.getStyleClass().add("rounded");
        sortIcon.getStyleClass().add("column-sort-icon");
        sortIcon.onMouseClickedProperty().set((event) -> {
            String symbol = ((MFXFontIcon) sortIcon.getIcon()).getDescription();

            resetSortingOrder();

            if (symbol == SortingIcons.NONE.symbol) {
                sortIcon.setIcon(SortingIcons.DESCENDING.newIcon());
                sortBy(new TableSourceSorting(name, Order.DESCENDING));
            } else if (symbol == SortingIcons.DESCENDING.symbol) {
                sortIcon.setIcon(SortingIcons.ASCENDING.newIcon());
                sortBy(new TableSourceSorting(name, Order.ASCENDING));
            } else {
                sortIcon.setIcon(SortingIcons.NONE.newIcon());
                sortBy(new TableSourceSorting(name, Order.NONE));
            }
        });
        RegionUtils.makeRegionCircular(sortIcon);

        column.setGraphic(sortIcon);
        column.setIconAlignment(HPos.RIGHT);

        return column;
    }

    public VirtualTable table() {
        return table;
    }

    public LedgerSource source() {
        return source;
    }

    // ----------------------------------------------------- private methods
    private void resetSortingOrder() {
        for (int i = 0; i < table.getColumns().size(); ++i) {
            DefaultTableColumn c = (DefaultTableColumn) table.getColumn(i);
            MFXIconWrapper w = (MFXIconWrapper) c.getGraphic();
            w.setIcon(SortingIcons.NONE.newIcon());
        }
    }

    public void fetch() {
        ledgerDialogPane.getScene().setCursor(Cursor.WAIT);
        ledgerDialogPane.disableProperty().set(true);

        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                source.fetch(); return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                ledgerDialogPane.getScene().setCursor(Cursor.DEFAULT);
                ledgerDialogPane.disableProperty().set(false);
            }
        };
        background.submit(task);
    }

    public void sortBy(final TableSourceSorting sorting) {
        ledgerDialogPane.getScene().setCursor(Cursor.WAIT);
        ledgerDialogPane.disableProperty().set(true);

        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                source.sortBy(sorting); return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                ledgerDialogPane.getScene().setCursor(Cursor.DEFAULT);
                ledgerDialogPane.disableProperty().set(false);
            }
        };
        background.submit(task);
    }

    // ------------------------------------------------------------- AltCell
    public static class AltCell<E> extends SimpleTableCell<Transaction, E> {

        public AltCell(Transaction item, Function<Transaction, E> extractor) {
            super(item, extractor);
        }

        public AltCell(Transaction item, Function<Transaction, E> extractor, StringConverter<E> converter) {
            super(item, extractor, converter);
        }

        @Override
        protected void init() {
            super.init();
            addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    TableColumn<Transaction, ? extends TableCell<Transaction>> column = getColumn();
                    VirtualTable<Transaction> table = column.getTable();
                    if (table.getColumnsLayoutMode() == ColumnsLayoutMode.FIXED) {
                        return;
                    }
                    table.getTableHelper().autosizeColumn(column);
                }
            });
        }

        @Override
        public void updateIndex(int index) {
            super.updateIndex(index);
            invalidate();
        }

        @Override
        public void updateRow(int rIndex, DefaultTableRow<Transaction> row) {
            super.updateRow(rIndex, row);
            invalidate();
        }

        @Override
        public void updateItem(Transaction item) {
            super.updateItem(item);
        }

        @Override
        public void invalidate() {
            if (getExtractor() == null) {
                label.setText(Optional.ofNullable(getRow()).map(TableRow::getIndex).orElse(-1).toString());
                return;
            }

            E e = getExtractor().apply(getItem());
            Platform.runLater(() -> {
                label.setText(getConverter().toString(e));
            });
        }
    }
}
