package ste.w3.easywallet.ui;

import ste.w3.easywallet.TestingUtils;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

/**
 *
 */
public class TransactionLedgerAsyncTest extends ApplicationTest implements TestingUtils {

    private static final int PAGE_SIZE = 10;

    private LedgerControllerEx controller;

    @Override
    public void start(Stage stage) throws Exception {
        Preferences preferences = givenEmptyPreferences();
        preferences.db = JDBC_CONNECTION_STRING;
        givenDatabase(new Wallet(WALLET1), 50);

        controller = new LedgerControllerEx();
        controller.source.pageSize(PAGE_SIZE);

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
        LatchedLedgerSource source = (LatchedLedgerSource)controller.source;

        source.hold();
        controller.table.scrollToLastRow(); scroll(VerticalDirection.UP); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.WAIT);
        source.go(); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.DEFAULT);
    }

    @Test
    public void async_and_wait_cursor_when_changing_sorting() {
        LatchedLedgerSource source = (LatchedLedgerSource)controller.source;

        source.hold();
        controller.sortBy(new TableSourceSorting("when", Order.DESCENDING));
        waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.WAIT);
        source.go(); waitForFxEvents();
        then(controller.ledgerDialogPane.getScene().getCursor()).isSameAs(Cursor.DEFAULT);
    }


    // --------------------------------------------------------- private methods

    // ------------------------------------------------------ LedgerControllerEx

    private class LedgerControllerEx extends LedgerController implements Initializable {
        public LedgerControllerEx() {
            super(null, new LatchedLedgerSource());
        }
    }

    // ----------------------------------------------------- LatchedLedgerSource

    private class LatchedLedgerSource extends LedgerSource {
        private CountDownLatch latch = null;

        public LatchedLedgerSource() {
            super(new Wallet(WALLET1));
        }

        public void hold() {
            latch = new CountDownLatch(1);
        }

        public void go() {
            latch.countDown();
        }

        @Override
        public void fetch() {
//            System.out.println("latch: " + latch);
            if (latch != null) {
                try {
//                    System.out.println("on hold");
                    latch.await(2, TimeUnit.SECONDS);
//                    System.out.println("let's go");
                } catch (InterruptedException x) {
                    x.printStackTrace();
                } finally {
                    latch = null;
                }
            }
//            System.out.println("fetching...");
            super.fetch();
        }
    }

}
