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

import ste.w3.easywallet.TestingUtils;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.RandomStringGenerator;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.EasyWalletException;
import ste.w3.easywallet.Labels;
import static ste.w3.easywallet.Labels.ERR_NETWORK;
import static ste.w3.easywallet.Labels.LABEL_BUTTON_OK;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;
import static ste.w3.easywallet.ui.Constants.KEY_ADD_WALLET;
import static ste.w3.easywallet.ui.Constants.KEY_REFRESH;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class EasyWalletMainUITest extends BaseEasyWalletMain implements TestingConstants, TestingUtils {


    @Test
    public void window_title_icon_and_size() throws Exception {
        Pane root = lookup(".root").queryAs(Pane.class);
        then(stage.getTitle()).isEqualTo("EasyWallet v0.1");
        then(root.getScene().getWidth()).isGreaterThanOrEqualTo(400);
        then(root.getScene().getHeight()).isGreaterThanOrEqualTo(600);
        then(stage.getIcons()).isNotEmpty();
    }


    @Test
    public void launch_with_wallets_shows_wallets_in_wallet_pane() throws Exception {
        NodeQuery q = lookup(".wallet_card");
        VBox[] cards = q.queryAll().toArray(new VBox[0]);

        then(cards).hasSize(1);
        Then.then(lookup("0x" + preferences.wallets[0].address)).hasWidgets();
    }

    @Test
    public void show_added_wallet_in_wallet_pane_and_save_prefs() throws Exception {
        clickOn('#' + KEY_ADD_WALLET);
        lookup(".mfx-text-field").queryAs(TextField.class).setText(TestingConstants.WALLET1);
        clickOn(LABEL_BUTTON_OK);

        waitForFxEvents();

        Then.then(lookup("0x" + TestingConstants.WALLET1)).hasWidgets();
        then(getPreferencesFile()).content().contains(
            String.format("{\"address\":\"%s\",\"privateKey\":\"\",\"mnemonicPhrase\":\"\",\"balances\":{}}", TestingConstants.WALLET1)
        );
    }

    @Test
    public void show_added_wallet_by_key_in_wallet_pane_and_save_prefs() throws Exception {
        clickOn('#' + KEY_ADD_WALLET) ; clickOn(Labels.LABEL_RADIO_PRIVATE_KEY);
        lookup(".mfx-text-field").queryAs(TextField.class).setText(PRIVATE_KEY1);
        clickOn(LABEL_BUTTON_OK);

        waitForFxEvents();

        Then.then(lookup("0x" + ADDRESS1)).hasWidgets();
        then(getPreferencesFile()).content().contains(
            String.format("{\"address\":\"%s\",\"privateKey\":\"%s\",\"mnemonicPhrase\":\"\",\"balances\":{}}", ADDRESS1, PRIVATE_KEY1)
        );
    }

    @Test
    public void remove_deleted_wallet_and_save_prefs() throws Exception {
        final String WALLET = main.getPreferences().wallets[0].address;
        clickOn("mfx-delete");
        waitForFxEvents();
        Then.then(lookup("0x" + WALLET)).hasNoWidgets();
        then(getPreferencesFile()).content().doesNotContain(WALLET);
    }

    @Test
    public void pressing_refresh_udates_balances_in_ui_and_preferences() throws Exception {
        server.addBalanceRequest(ETH, "0x" + preferences.wallets[0].address, new BigDecimal("47.34269121"));
        server.addBalanceRequest(STORJ, "0x" + preferences.wallets[0].address, new BigDecimal("534.09876543"));

        clickOn('#' + KEY_REFRESH);
        waitForFxEvents();

        Then.then(lookup("ETH 47.34269121 - STORJ 534.09876543")).hasWidgets();
        then(main.getConfigFile()).content().contains("{\"ETH\":47.34269121,\"STORJ\":534.09876543}");
    }

    @Test
    public void error_in_refresh_shows_error_pane_with_text() throws Exception {
        withConnectionException();

        Then.then(lookup(".error")).hasNoWidgets();
        clickOn('#' + Constants.KEY_REFRESH);

        Then.then(lookup(".error")).hasOneWidget();
        then(controller.errorLabel.getText())
            .contains(ERR_NETWORK)
            .contains("network not available");

        clickOn('#' + Constants.KEY_CLOSE_ERROR);
        Then.then(lookup(".error")).hasNoWidgets();
    }

}
