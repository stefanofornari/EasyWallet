package ste.w3.easywallet.ui;

import ste.w3.easywallet.TestingUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Preferences;
import static ste.w3.easywallet.TestingConstants.WALLET1;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.ledger.LedgerSource;
import ste.w3.easywallet.data.Order;
import ste.w3.easywallet.data.TableSourceSorting;
import ste.xtest.concurrent.PausableThreadPoolExecutor;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class TransactionLedgerAsyncTest extends ApplicationTest implements TestingUtils {

    private final PausableThreadPoolExecutor EXECUTOR = new PausableThreadPoolExecutor();

    private static final int PAGE_SIZE = 10;

    private LedgerController controller;

    @Override
    public void start(Stage stage) throws Exception {
        Preferences preferences = givenEmptyPreferences();
        preferences.db = givenDatabase(new Wallet(WALLET1), 50);

        controller = new LedgerController(null, new LedgerSource(new Wallet(WALLET1)));
        controller.source.pageSize(PAGE_SIZE);
        PrivateAccess.setInstanceValue(controller, "background", EXECUTOR);

        Scene scene = new Scene(
            FXMLLoader.load(
                this.getClass().getResource("/fxml/LedgerDialog.fxml"),
                null, null,
                (clazz) -> controller
            ),
            640, 350
        );
        stage.setScene(scene);
        stage.show();

        controller.fetch(); waitForFxEvents();
    }

    @Test
    public void async_and_wait_cursor_when_fetching() {
        EXECUTOR.hold();
        controller.table.scrollToLastRow(); scroll(VerticalDirection.UP); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.WAIT);
        EXECUTOR.go(); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.DEFAULT);
    }

    @Test
    public void async_and_wait_cursor_when_changing_sorting() {
        EXECUTOR.hold();
        controller.sortBy(new TableSourceSorting("when", Order.DESCENDING));
        waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.WAIT);
        EXECUTOR.go(); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.DEFAULT);
    }

}
