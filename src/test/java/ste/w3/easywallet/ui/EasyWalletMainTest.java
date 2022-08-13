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
import java.util.Set;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import static ste.w3.easywallet.Labels.LABEL_OK;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import static ste.w3.easywallet.ui.Constants.KEY_ADD_WALLET;
import static ste.w3.easywallet.ui.Constants.KEY_REFRESH;

/**
 *
 * @author ste
 */
public class EasyWalletMainTest extends ApplicationTest {

    EasyWalletMain main;
    Preferences preferences;
    Stage stage;

    private static final String CONFIG_FILE = ".config/ste.w3.easywallet/predferences.json";

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            preparePreferences();
        } catch (IOException x) {
            x.printStackTrace();
        }

        main = new EasyWalletMainWithPreferences();

        main.start(stage);
    }

    @Test
    public void should_contain_button_with_text() throws Exception {
        Pane root = lookup(".root").queryAs(Pane.class);
        then(stage.getTitle()).isEqualTo("EasyWallet v0.1");
        then(root.getScene().getWidth()).isGreaterThanOrEqualTo(400);
        then(root.getScene().getHeight()).isGreaterThanOrEqualTo(600);

        Button b = lookup('#' + KEY_ADD_WALLET).queryButton();
        then(b.getStyleClass().toArray()).contains("primary-button");
        Then.then(b).hasText("+");

        b = lookup('#' + KEY_REFRESH).queryButton();
        then(b.getStyleClass().toArray()).contains("primary-button");
        Then.then(b).hasText("⟳").isDisabled();
    }

    @Test
    public void launch_with_wallets_shows_wallets_in_wallet_pane() throws Exception {
        NodeQuery q = lookup(".wallet_card");
        VBox[] cards = q.queryAll().toArray(new VBox[0]);

        then(cards).hasSize(1);
        Set<Label> labels = from(cards).lookup(".label").queryAllAs(Label.class);
        then(labels).extracting(Label::getText).contains("0x" + preferences.wallets[0].address);
    }

    @Test
    public void show_added_wallet_in_wallet_pane() throws Exception {
        clickOn('#' + KEY_ADD_WALLET);
        lookup(".mfx-text-field").queryAs(TextField.class).setText(TestingConstants.WALLET1);
        clickOn(LABEL_OK);

        waitForFxEvents();

        Then.then(lookup("0x" + TestingConstants.WALLET1)).hasWidgets();
        then(getPreferencesFile()).content().contains(
            String.format("{\"address\":\"%s\",\"privateKey\":\"\",\"mnemonicPhrase\":\"\"}", TestingConstants.WALLET1)
        );
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
        preferences.endpoint = randomStringGenerator.generate(20);
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[] { new Wallet(randomStringGenerator.generate(40)) };

        PreferencesManager pm = new PreferencesManager();

        FileUtils.writeStringToFile(preferencesFile, pm.toJSON(preferences), "UTF-8");
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
