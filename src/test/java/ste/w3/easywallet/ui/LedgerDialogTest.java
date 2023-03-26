package ste.w3.easywallet.ui;

import io.github.palexdev.materialfx.controls.MFXButton;
import ste.w3.easywallet.TestingUtils;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.mfxcore.controls.MFXIconWrapper;
import io.github.palexdev.virtualizedfx.table.VirtualTable;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Labels;
import static ste.w3.easywallet.Labels.LABEL_BUTTON_CLOSE;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingConstants;
import static ste.w3.easywallet.TestingConstants.ADDRESS3;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class LedgerDialogTest extends ApplicationTest implements Labels, TestingConstants, TestingUtils {
    private static final Wallet WALLET = new Wallet(ADDRESS3);
    private static final String TITLE = String.format("0x%s's incoming token transfers", ADDRESS3);

    private static final int PAGE_SIZE = 10;

    private LedgerDialog dialog;
    private LedgerController controller;

    @Override
    public void start(Stage stage) throws Exception {
        Preferences preferences = givenEmptyPreferences();
        preferences.coins = new Coin[] { STORJ, GLM };
        preferences.db = givenDatabase(WALLET);;

        Pane mainWindow = new BorderPane();
        showInStage(stage, mainWindow);

        dialog = new LedgerDialog(mainWindow, WALLET);

        controller = (LedgerController)dialog.controller;
        controller.source.pageSize(PAGE_SIZE);

        dialog.show();

        //
        // Let's make sure we have to scroll down
        //
        Pane contentPane = (Pane)dialog.getScene().lookup("#ledgerPane");
        contentPane.setMinHeight(200);
        contentPane.setMaxHeight(200);
        contentPane.setPrefHeight(200);
        // ///

        controller.fetch(); waitForFxEvents();
    }

    @Test
    public void ledger_dialog_widgets_and_controller() {
        final String[] TITLES = { "when", "coin", "amount", "hash"};

        Then.then(lookup(TITLE)).hasWidgets();
        Then.then(lookup(LABEL_BUTTON_CLOSE)).hasWidgets();
        Then.then(lookup(".virtual-table")).hasOneWidget();

        then(controller).isNotNull().isInstanceOf(LedgerController.class);
        then(controller.table.getColumns()).hasSize(TITLES.length);
        then(lookup(LABEL_BUTTON_CLOSE).queryAs(MFXButton.class).getStyleClass()).contains("primary-button");

        for (int i=0; i<TITLES.length; ++i) {
            DefaultTableColumn column = (DefaultTableColumn)controller.table.getColumns().get(i);
            Then.then(column).hasText(TITLES[i]);
        }
    }

    @Test
    public void dialog_size() {
        then(dialog.getWidth()).isLessThan(1000);
        then(dialog.getHeight()).isLessThan(800);
    }

    @Test
    public void pressing_close_closes_the_dialog() throws Exception {
        clickOn(LABEL_BUTTON_CLOSE); waitForFxEvents();
        Then.then(lookup(TITLE)).hasNoWidgets();
    }

    @Test
    public void close_on_ESC() {
        type(KeyCode.ESCAPE);
        then(dialog.isShowing()).isFalse();
    }

    @Test
    public void columns_shows_names() throws Exception {
        //
        // we want the table and columns to be displayed even if there is no data
        //
        givenEmptyDatabase();
        controller.fetch(); waitForFxEvents();
        ///

        Then.then(lookup(".table-column")).hasNWidgets(4);
        Then.then(lookup(".table-column").lookup(".column-sort-icon")).hasNWidgets(4);
        Then.then(lookup(".table-column").lookup("when")).hasWidgets();
        Then.then(lookup(".table-column").lookup("amount")).hasWidgets();
        Then.then(lookup(".table-column").lookup("coin")).hasWidgets();
        Then.then(lookup(".table-column").lookup("hash")).hasWidgets();

        //
        // Columns sorting icon is initially to no-sort
        //
        then(lookup(".table-column").lookup(".column-sort-icon").queryAllAs(MFXIconWrapper.class))
            .allMatch(
                (wrapper) -> {
                    MFXFontIcon icon = (MFXFontIcon)wrapper.getIcon();
                    //
                    // == used on purpose to make sure SortingIcons is used
                    //
                    return SortingIcons.NONE.symbol == icon.getDescription();
                }
            );
    }

    @Test
    public void show_tablesource_content() {
        final VirtualTable<Transaction> table = lookup(".virtual-table").query();
        final ObservableList<Transaction> items = table.getItems();

        then(items).hasSize(10);
        List<Transaction> walletTransactions = walletTransactions();
        for (int i=0; i<10; ++i) {
            then(items.get(i).hash).isEqualTo(walletTransactions.get(i).hash);
        }
    }

    @Test
    /**
     * Only one column can be selected for sorting (@see
     */
    public void sorting_icons_change_when_clicked() {
        MFXIconWrapper[] columns =
            lookup(".table-column").lookup(".column-sort-icon")
            .queryAllAs(MFXIconWrapper.class).toArray(new MFXIconWrapper[0]);

        for (MFXIconWrapper wrapper: columns) {
            clickOn(wrapper); waitForFxEvents();
            then(wrapper.getIcon()).extracting("description").isSameAs(SortingIcons.DESCENDING.symbol);
            clickOn(wrapper); waitForFxEvents();
            then(wrapper.getIcon()).extracting("description").isSameAs(SortingIcons.ASCENDING.symbol);
            clickOn(wrapper); waitForFxEvents();
            then(wrapper.getIcon()).extracting("description").isSameAs(SortingIcons.NONE.symbol);
        }

        //
        // when one column is clicked all other columns shall be reset to NONE
        //
        clickOn(columns[0]); waitForFxEvents();
        for (int i=1; i<columns.length; ++i) {
            then(columns[i].getIcon()).extracting("description").isSameAs(SortingIcons.NONE.symbol);
        }
        clickOn(columns[columns.length-1]); waitForFxEvents();
        for (int i=0; i<columns.length-1; ++i) {
            then(columns[i].getIcon()).extracting("description").isSameAs(SortingIcons.NONE.symbol);
        }
    }

    @Test
    public void update_items_when_sorting_changes() {
        final ObservableList<Transaction> items = controller.table().getItems();

        MFXIconWrapper sortingIcon =
            lookup(".table-column").lookup("hash").lookup(".column-sort-icon")
            .queryAs(MFXIconWrapper.class);

        clickOn(sortingIcon);  waitForFxEvents();

        then(items).isSortedAccordingTo((t1, t2) -> t2.hash.compareTo(t1.hash));

        sortingIcon =
            lookup(".table-column").lookup("when").lookup(".column-sort-icon")
            .queryAs(MFXIconWrapper.class);
        clickOn(sortingIcon); waitForFxEvents();

        then(items).isSortedAccordingTo((t1, t2) -> t2.when.compareTo(t1.when));

        //
        // unsort
        //
        clickOn(sortingIcon); clickOn(sortingIcon); waitForFxEvents();

        List<Transaction> walletTransactions = walletTransactions();
        for (int i=0; i<PAGE_SIZE; ++i) {
            then(items.get(i).hash).isEqualTo(walletTransactions.get(i).hash);
        }
    }

    @Test
    public void scrolling_up_at_the_bottom_fetches_more_data() {
        final VirtualTable<Transaction> table = lookup(".virtual-table").query();
        final ObservableList<Transaction> items = table.getItems();

        table.scrollToLastRow(); moveTo(table); scroll(VerticalDirection.UP);

        List<Transaction> walletTransactions = walletTransactions();
        for (int i=0; i<20; ++i) {
            then(items.get(i).hash).isEqualTo(walletTransactions.get(i).hash);
        }

        then(table.getLastRowsRange().getMax()).isEqualTo(15);
    }

    @Test
    public void scrolling_down_at_the_bottom_does_not_fetch_more_data() {
        final VirtualTable<Transaction> table = lookup(".virtual-table").query();
        final ObservableList<Transaction> items = table.getItems();

        table.scrollToLastRow(); moveTo(table); scroll(VerticalDirection.DOWN);

        then(items).hasSize(PAGE_SIZE);
    }

    // --------------------------------------------------------- private methods

    private List<Transaction> walletTransactions() {
        return transactions.stream().filter((t) -> ADDRESS3.equalsIgnoreCase(t.to)).toList();
    }

}
