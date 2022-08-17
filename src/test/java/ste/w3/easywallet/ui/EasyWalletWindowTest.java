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
import javafx.scene.control.Button;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import static ste.w3.easywallet.ui.Constants.KEY_ADD_WALLET;
import static ste.w3.easywallet.ui.Constants.KEY_REFRESH;

/**
 *
 */
public class EasyWalletWindowTest extends ApplicationTest implements TestingConstants, TestingUtils {

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
    }

    @Test
    public void initial_state() throws Exception {
        showInStageLater(stage, new EasyWalletFXMLLoader().loadMainWindow(new EasyWalletMain()));

        Button b = lookup('#' + KEY_ADD_WALLET).queryButton();
        then(b.getStyleClass().toArray()).contains("primary-button");
        Then.then(b).hasText("+");

        b = lookup('#' + KEY_REFRESH).queryButton();
        then(b.getStyleClass().toArray()).contains("primary-button");
        Then.then(b).hasText("⟳").isDisabled();

        Then.then(lookup(".error")).hasNoWidgets();
    }

    @Test
    public void no_wallet_no_card() throws Exception {
        showInStageLater(stage, new EasyWalletFXMLLoader().loadMainWindow(new EasyWalletMain()));
        Then.then(lookup(".wallet_card")).hasNoWidgets();
        Then.then(lookup('#' + KEY_REFRESH).queryButton()).isDisabled();
    }

    @Test
    public void configured_wallets_have_a_card_each() throws Exception {
        EasyWalletMain main = new EasyWalletMain();
        main.getPreferences().wallets = new Wallet[] {
            new Wallet(ADDRESS1), new Wallet(ADDRESS2), new Wallet(ADDRESS3)
        };

        showInStageLater(stage, new EasyWalletFXMLLoader().loadMainWindow(main));

        Then.then(lookup(".wallet_card")).hasNWidgets(3);
        Then.then(lookup('#' + KEY_REFRESH).queryButton()).isEnabled();
    }

    @Test
    public void show_add_wallet_dialog() throws Exception {
        showInStageLater(stage, new EasyWalletFXMLLoader().loadMainWindow(new EasyWalletMain()));
        Then.then(lookup(".mfx-dialog")).hasNoWidgets();
        clickOn("#" + KEY_ADD_WALLET); waitForFxEvents();
        Then.then(lookup(".mfx-dialog")).hasOneWidget();
    }


    // --------------------------------------------------------- private methods

}
