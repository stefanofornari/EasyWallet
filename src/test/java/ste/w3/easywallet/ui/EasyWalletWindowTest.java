/*
 * Copyright (C) 2022 Stefano Fornari.
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

import java.io.IOException;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EasyWalletWindowTest extends ApplicationTest implements TestingConstants {

    EasyWalletMainController controller;

    @Override
    public void start(Stage stage) throws IOException {
        Pane mainWindow = new EasyWalletFXMLLoader().loadMainWindow(new Wallet[0]);
        stage.setScene(new Scene(mainWindow));
        controller = (EasyWalletMainController)mainWindow.getUserData();
        stage.show();
    }

    @Before
    public void before() {
        controller.wallets.clear();
    }

    @Test
    public void no_wallet_no_card() throws Exception {
        Then.then(lookup(".wallet_card")).hasNoWidgets();
    }

    @Test
    public void configured_wallets_have_a_card_each() throws Exception {
        final Wallet[] WALLTES = new Wallet[] {
            new Wallet(ADDRESS1), new Wallet(ADDRESS2), new Wallet(ADDRESS3)
        };

        controller.wallets.addAll(WALLTES);

        waitForFxEvents();

        Then.then(lookup(".wallet_card")).hasNWidgets(WALLTES.length);
    }

    @Test
    public void show_add_wallet_dialog() throws Exception {
        waitForFxEvents();
        Then.then(lookup(".mfx-dialog")).hasNoWidgets();

        clickOn("#btn_add_wallet"); waitForFxEvents();
        Then.then(lookup(".mfx-dialog")).hasOneWidget();

    }

}
