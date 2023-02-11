package ste.w3.easywallet.ui;

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import ste.w3.easywallet.TestingUtils;
import io.github.palexdev.mfxcore.controls.MFXIconWrapper;
import io.github.palexdev.virtualizedfx.table.defaults.DefaultTableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static ste.w3.easywallet.TestingConstants.ADDRESS3;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.ledger.LedgerSource;
import ste.w3.easywallet.data.Order;

/**
 *
 */
public class LedgerControllerTest extends ApplicationTest implements TestingUtils {

    private static final Wallet WALLET = new Wallet(ADDRESS3);

    private LedgerController controller;
    private LedgerDialog dialog;

    @Override
    public void start(Stage stage) throws Exception {
        givenDatabase(WALLET);

        Pane mainWindow = new BorderPane();
        showInStageLater(stage, mainWindow);

        dialog = new LedgerDialog(mainWindow, WALLET);
        //
        // Setting max size otherwise monocle gets in the error:
        // Exception in thread "JavaFX Application Thread" java.nio.BufferOverflowException
        // at java.nio.IntBuffer.put(IntBuffer.java:769)
        // at com.sun.glass.ui.monocle.Framebuffer.composePixels(Framebuffer.java:187)
        // ...
        dialog.setMaxWidth(800);
        dialog.setMaxHeight(400);

        dialog.show();

        controller = (LedgerController)dialog.controller;

        controller.fetch();
    }

    @Test
    public void columns_setup_after_initialization() throws Exception {
        then(controller.table().getColumns()).hasSize(4);
        Then.then((DefaultTableColumn)controller.table().getColumn(0)).hasText("when");
        Then.then((DefaultTableColumn)controller.table().getColumn(1)).hasText("coin");
        Then.then((DefaultTableColumn)controller.table().getColumn(2)).hasText("amount");
        Then.then((DefaultTableColumn)controller.table().getColumn(3)).hasText("hash");
    }

    @Test
    public void source_sorting_column_changes_when_column_is_clicked() {
        DefaultTableColumn c = (DefaultTableColumn)controller.table().getColumn(0);
        MFXIconWrapper icon = (MFXIconWrapper)c.getGraphic();

        //
        // Clicking once creates a new sorting specification (descending) for the column
        //
        clickOn(icon);
        then(controller.source.sorting().column()).isEqualTo("when");
        then(controller.source.sorting().order()).isEqualTo(Order.DESCENDING);

        //
        // Clicking twice changes the sorting specification for the column to descending
        //
        clickOn(icon);
        then(controller.source.sorting().column()).isEqualTo("when");
        then(controller.source.sorting().order()).isEqualTo(Order.ASCENDING);

        //
        // Clicking the third time removes the sorting specification for the column
        //
        clickOn(icon);
        then(controller.source.sorting()).isNull();

        //
        // Clicking other columns changes the sorting specifications
        //
        c = (DefaultTableColumn)controller.table().getColumn(1);
        clickOn((MFXIconWrapper)c.getGraphic());
        then(controller.source.sorting().column()).isEqualTo(c.getText());
        then(controller.source.sorting().order()).isEqualTo(Order.DESCENDING);

        c = (DefaultTableColumn)controller.table().getColumn(3);
        clickOn((MFXIconWrapper)c.getGraphic()); clickOn((MFXIconWrapper)c.getGraphic());
        then(controller.source.sorting().column()).isEqualTo(c.getText());
        then(controller.source.sorting().order()).isEqualTo(Order.ASCENDING);
    }

    @Test
    public void source_getter() {
        //
        // default
        //
        then(controller.source).isNotNull();
        then(controller.source()).isSameAs(controller.source);

        //
        // custom
        //
        LedgerSource s = new LedgerSource(WALLET);
        LedgerController c = new LedgerController((MFXGenericDialog)dialog.getContent(), s);
        then(c.source).isSameAs(s);
        then(c.source()).isSameAs(s);
    }

}
