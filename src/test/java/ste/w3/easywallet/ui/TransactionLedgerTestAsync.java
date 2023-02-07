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
import ste.w3.easywallet.ledger.LedgerSource;
import ste.w3.easywallet.ledger.Order;
import ste.w3.easywallet.data.TableSourceSorting;

/**
 *
 */
public class TransactionLedgerTestAsync extends ApplicationTest implements TestingUtils {

    private static final int PAGE_SIZE = 10;

    private LedgerControllerEx controller;

    public TransactionLedgerTestAsync() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        givenDatabase();

        controller = new LedgerControllerEx();
        controller.source.pageSize(PAGE_SIZE);

        Scene scene = new Scene(
            FXMLLoader.load(
                this.getClass().getResource("/fxml/TransactionLedger.fxml"),
                null, null,
                (clazz) -> controller
            ),
            640, 480
        );
        stage.setScene(scene);
        stage.show();

        controller.fetch(); waitForFxEvents();
    }

    @Test
    public void async_and_wait_cursor_when_fetching() {
        LatchedLedgerSource source = (LatchedLedgerSource)controller.source;

        source.hold();
        controller.table.scrollToLastRow(); scroll(VerticalDirection.DOWN); waitForFxEvents();
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

    // ----------------------------------------------------- DelayedLedgerSource

    private class LatchedLedgerSource extends LedgerSource {
        private CountDownLatch latch = null;

        public LatchedLedgerSource() {
            super();
        }

        public void hold() {
            latch = new CountDownLatch(1);
        }

        public void go() {
            latch.countDown();
        }

        @Override
        public void fetch() {
            if (latch != null) {
                try {
                    //System.out.println("on hold");
                    latch.await(2, TimeUnit.SECONDS);
                    //System.out.println("let's go");
                } catch (InterruptedException x) {
                    x.printStackTrace();
                } finally {
                    latch = null;
                }
            }
            //System.out.println("fetching...");
            super.fetch();
        }
    }

}
