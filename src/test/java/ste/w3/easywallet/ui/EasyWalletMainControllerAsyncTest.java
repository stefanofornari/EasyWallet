package ste.w3.easywallet.ui;

import ste.w3.easywallet.TestingUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.TestingConstants;
import ste.xtest.concurrent.PausableThreadPoolExecutor;
import ste.xtest.concurrent.WaitFor;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class EasyWalletMainControllerAsyncTest
extends BaseEasyWalletMain
implements TestingConstants, TestingUtils {

    private final PausableThreadPoolExecutor EXECUTOR = new PausableThreadPoolExecutor();

    @Override
    public void start(Stage stage) throws Exception {
        givenServer();
        givenEmptyDatabase();
        givenPreferences();
        givenMainWindow().initialize();
        givenRequests();

        controller = new EasyWalletMainController(main);
        PrivateAccess.setInstanceValue(controller, "background", EXECUTOR);

        Scene scene = new Scene(
            FXMLLoader.load(
                this.getClass().getResource("/fxml/EasyWalletMain.fxml"),
                null, null,
                (clazz) -> controller
            ),
            400, 500
        );
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void async_and_wait_cursor_when_refreshing() throws Exception {
        EXECUTOR.hold();

        clickOn("#btn_refresh"); waitForFxEvents();
        then(controller.refreshButton.getCursor()).isSameAs(Cursor.WAIT);
        Then.then(controller.refreshButton).isDisabled();

        EXECUTOR.go(); waitForRefresh();

        waitForFxEvents();
        then(controller.refreshButton.getCursor()).isNull();
        Then.then(controller.refreshButton).isEnabled();
    }

    // --------------------------------------------------------- private methods

}
