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
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.Wallet;

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
            prepareConfiguration();
        } catch (IOException x) {
            x.printStackTrace();
        }

        main = new EasyWalletMainWithPreferences();

        //
        // not great, but needed to suppress
        // java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.IllegalStateException: Cannot set style once stage has been set visible
        //
        try {
            main.start(stage);
        } catch (Throwable t) {
            System.out.println("suppressiong throwable " + t);
        }
    }
    /*

    @Test
    public void should_contain_button_with_text() throws Exception {
        JFXDecorator root = lookup(".root").queryAs(JFXDecorator.class);
        then(stage.getTitle()).isEqualTo("EasyWallet v0.1");
        then(root.getScene().getWidth()).isEqualTo(400);
        then(root.getScene().getHeight()).isEqualTo(600);

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
    */

    // --------------------------------------------------------- private methods

    private File getPreferencesFile() throws IOException {
        return new File(HOME.getRoot(), CONFIG_FILE);
    }

    private void prepareConfiguration() throws IOException {
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
