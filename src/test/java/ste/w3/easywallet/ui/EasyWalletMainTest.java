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

import java.io.File;
import java.io.IOException;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
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
public class EasyWalletMainTest extends ApplicationTest implements TestingConstants, TestingUtils {

    public static TestingServer server = null;

    private EasyWalletMain main;
    private Preferences preferences;
    private Stage stage;
    private EasyWalletMainController controller;

    private static final String CONFIG_FILE = ".config/ste.w3.easywallet/predferences.json";

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        server = new TestingServer();

        try {
            preparePreferences();
        } catch (IOException x) {
            x.printStackTrace();
        }

        main = new EasyWalletMainWithPreferences(); main.start(stage);

        controller = getController(lookup("#main").queryAs(Pane.class));
    }

    @Test
    public void window_titlwe_and_size() throws Exception {
        Pane root = lookup(".root").queryAs(Pane.class);
        then(stage.getTitle()).isEqualTo("EasyWallet v0.1");
        then(root.getScene().getWidth()).isGreaterThanOrEqualTo(400);
        then(root.getScene().getHeight()).isGreaterThanOrEqualTo(600);
    }


    @Test
    public void launch_with_wallets_shows_wallets_in_wallet_pane() throws Exception {
        NodeQuery q = lookup(".wallet_card");
        VBox[] cards = q.queryAll().toArray(new VBox[0]);

        then(cards).hasSize(1);
        Then.then(lookup("0x" + preferences.wallets[0].address)).hasWidgets();
        Then.then(lookup("0.0")).hasWidgets();
    }

    @Test
    public void show_added_wallet_in_wallet_pane_and_save_prefs() throws Exception {
        clickOn('#' + KEY_ADD_WALLET);
        lookup(".mfx-text-field").queryAs(TextField.class).setText(TestingConstants.WALLET1);
        clickOn(LABEL_BUTTON_OK);

        waitForFxEvents();

        Then.then(lookup("0x" + TestingConstants.WALLET1)).hasWidgets();
        then(getPreferencesFile()).content().contains(
            String.format("{\"address\":\"%s\",\"privateKey\":\"\",\"mnemonicPhrase\":\"\"}", TestingConstants.WALLET1)
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
            String.format("{\"address\":\"%s\",\"privateKey\":\"%s\",\"mnemonicPhrase\":\"\"}", ADDRESS1, PRIVATE_KEY1)
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
    public void pressing_refresh_udates_balances() {
        server.addBalance("0x" + preferences.wallets[0].address, "0x7bad706cf4a42e0055045");
        clickOn('#' + KEY_REFRESH);
        waitForFxEvents();
        Then.then(lookup("9344807.44378454")).hasWidgets();
    }

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
            "{\"endpoint\":\"%s\",\"appkey\":\"%s\",\"wallets\":[{\"address\":\"%s\",\"privateKey\":\"\",\"mnemonicPhrase\":\"\"}]}",
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
            "{\"endpoint\":\"new endpoint\",\"appkey\":\"new key\",\"wallets\":[{\"address\":\"1234567890123456789012345678901234567890\",\"privateKey\":\"\",\"mnemonicPhrase\":\"mnemonic1\"},{\"address\":\"0123456789012345678901234567890123456789\",\"privateKey\":\"privatekey2\",\"mnemonicPhrase\":\"mnemonic2\"}]}"
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
        then(wm.endpoint).isEqualTo(preferences.endpoint);
        then(wm.appkey).isEqualTo(preferences.appkey);
        //
        // NOTE: I do not need to do it with different values because
        // preparePreferences() creates always random values, therefore two
        // different executions provide already a requirement for the mutable
        // behaviour
        //
    }

    @Test
    public void error_in_refresh_shows_error_pane_with_text() throws Exception {
        withIOException();

        Then.then(lookup(".error")).hasNoWidgets();
        clickOn('#' + Constants.KEY_REFRESH);

        Then.then(lookup(".error")).hasOneWidget();
        then(controller.errorLabel.getText())
            .contains(ERR_NETWORK)
            .contains("network not available");

        clickOn('#' + Constants.KEY_CLOSE_ERROR);
        Then.then(lookup(".error")).hasNoWidgets();
    }

    // --------------------------------------------------------- private methods

    private File getPreferencesFile() throws IOException {
        return new File(HOME.getRoot(), CONFIG_FILE);
    }

    private void preparePreferences() throws IOException {
        File preferencesFile = getPreferencesFile();

        preferencesFile.getParentFile().mkdirs();

        //
        // Create some randomness to make sure the content is correctly read
        //
        RandomStringGenerator randomStringGenerator =
            new RandomStringGenerator.Builder()
                    .withinRange('0', 'f')
                    .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                    .build();
        preferences = new Preferences();
        preferences.endpoint = server.ethereum.url("v3/" + randomStringGenerator.generate(20)).toString();
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[] { new Wallet(randomStringGenerator.generate(40)) };

        PreferencesManager pm = new PreferencesManager();

        FileUtils.writeStringToFile(preferencesFile, pm.toJSON(preferences), "UTF-8");
    }

    private void withIOException() throws Exception {
        PrivateAccess.setInstanceValue(main, "walletManager", new WalletManager("http://somewere.com", "key") {
            @Override
            public WalletManager balance(Wallet wallet) throws IOException {
                throw new IOException("network not available");
            }

        });
    }


    // ------------------------------------------- EasyWalletMainWithPreferences

    private class EasyWalletMainWithPreferences extends EasyWalletMain {

        @Override
        protected File getConfigFile() {
            try {
                return getPreferencesFile();
            } catch (IOException x) {
                x.printStackTrace();
            }

            return null;
        }

    }

}
