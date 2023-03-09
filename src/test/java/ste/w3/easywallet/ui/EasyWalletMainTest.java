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
import javax.naming.InitialContext;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import static ste.w3.easywallet.Labels.ERR_NETWORK;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class EasyWalletMainTest extends BaseEasyWalletMain implements TestingConstants, TestingUtils {


    @Test
    public void save_preferences() throws Exception {
        final String TEST_KEY = "new key";
        final String TEST_EP = "new endpoint";
        final Wallet[] TEST_WALLETS = new Wallet[] {
            new Wallet(TestingConstants.WALLET1), new Wallet(TestingConstants.WALLET2)
        };
        TEST_WALLETS[0].mnemonicPhrase = "mnemonic1";
        TEST_WALLETS[1].mnemonicPhrase = "mnemonic2";
        TEST_WALLETS[1].privateKey = "privatekey1";
        TEST_WALLETS[1].privateKey = "privatekey2";

        //
        // actual preferences
        //
        Preferences p = main.getPreferences();
        main.savePreferences();
        then(getPreferencesFile()).content().isEqualTo(String.format(
            "{\"endpoint\":\"%s\",\"appkey\":\"%s\",\"wallets\":[{\"address\":\"%s\",\"privateKey\":\"\",\"mnemonicPhrase\":\"\",\"balances\":{}}],\"coins\":[{\"symbol\":\"ETH\",\"name\":\"Ether\",\"decimals\":18},{\"contract\":\"14F2c84A58e065C846c5fDDdadE0d3548F97A517\",\"symbol\":\"STORJ\",\"name\":\"StorjToken\",\"decimals\":8}],\"db\":\"\"}",
            p.endpoint, p.appkey, p.wallets[0].address
        ));

        //
        // new prefrences
        //
        p.appkey = TEST_KEY;
        p.endpoint = TEST_EP;
        p.wallets = TEST_WALLETS;

        main.savePreferences();
        then(getPreferencesFile()).content().isEqualTo(
            "{\"endpoint\":\"new endpoint\",\"appkey\":\"new key\",\"wallets\":[{\"address\":\"1234567890123456789012345678901234567890\",\"privateKey\":\"\",\"mnemonicPhrase\":\"mnemonic1\",\"balances\":{}},{\"address\":\"0123456789012345678901234567890123456789\",\"privateKey\":\"privatekey2\",\"mnemonicPhrase\":\"mnemonic2\",\"balances\":{}}],\"coins\":[{\"symbol\":\"ETH\",\"name\":\"Ether\",\"decimals\":18},{\"contract\":\"14F2c84A58e065C846c5fDDdadE0d3548F97A517\",\"symbol\":\"STORJ\",\"name\":\"StorjToken\",\"decimals\":8}],\"db\":\"\"}"
        );
    }

    @Test
    public void add_wallet_to_preferences() {
        Preferences p = main.getPreferences();

        main.addWallet(new Wallet(TestingConstants.WALLET1));
        then(p.wallets).hasSize(2);
        then(p.wallets[1].address).isEqualTo(TestingConstants.WALLET1);

        main.addWallet(new Wallet(TestingConstants.WALLET2));
        then(p.wallets).hasSize(3);
        then(p.wallets[2].address).isEqualTo(TestingConstants.WALLET2);
    }

    @Test
    public void create_wallet_manager_from_preferences() throws Exception {
        WalletManager wm = main.getWalletManager();

        then(wm).isNotNull();
        then(wm.endpoint).isEqualTo(preferences.url());
        //
        // NOTE: I do not need to do it with different values because
        // preparePreferences() creates always random values, therefore two
        // different executions provide already a requirement for the mutable
        // behaviour
        //
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

    @Test
    public void bind_preferences() throws Exception {
        InitialContext ctx = new InitialContext();

        Preferences p = (Preferences)ctx.lookup(main.hashCode() + "/preferences");
        then(p).isNotNull().isSameAs(main.getPreferences());
    }

}
