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
package ste.w3.easywallet.ui;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import static ste.w3.easywallet.TestingConstants.ADDRESS3;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class LedgerDialogTest extends ApplicationTest implements Labels, TestingConstants, TestingUtils {

    private static final Wallet WALLET = new Wallet(ADDRESS3);
    private static final String TITLE = String.format("0x%s's incoming token transfers", ADDRESS3);

    private LedgerDialog dialog;

    @Override
    public void start(Stage stage) throws Exception {
        Pane mainWindow = new BorderPane();
        showInStage(stage, mainWindow);

        dialog = new LedgerDialog(mainWindow, WALLET);
        dialog.show();
    }

    @Test
    public void ledger_dialog_widgets_and_controller() {
        LedgerController controller = (LedgerController)dialog.controller;

        //
        // The OK button is disabled until there is a valid address
        //

        Then.then(lookup(TITLE)).hasWidgets();
        Then.then(lookup(LABEL_BUTTON_CLOSE)).hasWidgets();
        Then.then(lookup(".mfx-paginated-table-view")).hasOneWidget();

        then(controller).isNotNull().isInstanceOf(LedgerController.class);
        then(controller.transactions.getTableColumns()).hasSize(3);
        then(lookup(LABEL_BUTTON_CLOSE).queryAs(MFXButton.class).getStyleClass()).contains("primary-button");

        final String[] TITLES = { "when", "amount", "from"};
        for (int i=0; i<TITLES.length; ++i) {
            MFXTableColumn column = (MFXTableColumn)controller.transactions.getTableColumns().get(i);
            Then.then(column).hasText(TITLES[i]);
        }
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

}
