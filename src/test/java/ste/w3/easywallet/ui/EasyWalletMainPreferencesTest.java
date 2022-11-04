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
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.Wallet;
import static ste.w3.easywallet.ui.Constants.CONFIG_FILE;

/**
 *
 * @author ste
 */
public class EasyWalletMainPreferencesTest extends ApplicationTest {

    EasyWalletMain main;
    Preferences preferences;
    Stage stage;

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

        main.start(stage);
    }

    @Test
    public void preferences_file_default() {
        then(new EasyWalletMain().configFile).isEqualTo(
            new File(FileUtils.getUserDirectory(), CONFIG_FILE)
        );
    }

    @Test
    public void read_configuration_at_startup() {
        Preferences preferences = main.getPreferences();
        then(preferences).isNotNull();
        then(preferences.endpoint).isEqualTo(preferences.endpoint);
        then(preferences.appkey).isEqualTo(preferences.appkey);
        then(preferences.wallets).hasSize(1);
        then(preferences.wallets[0].address).isEqualTo(preferences.wallets[0].address);
    }

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
