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
 */
public class EasyWalletMainPreferencesTest extends ApplicationTest {

    private EasyWalletMain main;
    private Preferences preferences;
    private Stage stage;

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();


    @Override
    //
    // TODO: remove throws Exception when fixed error handling
    //
    public void start(Stage stage) throws Exception {
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
        Preferences p = main.getPreferences();
        then(p).isNotNull();
        then(p.endpoint).isEqualTo(preferences.endpoint);
        then(p.appkey).isEqualTo(preferences.appkey);
//        then(p.wallets).hasSize(1);
        then(p.wallets[0].address).isEqualTo(preferences.wallets[0].address);
        then(p.db).isEqualTo(preferences.db);
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
        preferences.endpoint = "http://an.endpoint.com/"+randomStringGenerator.generate(20);
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[] { new Wallet(randomStringGenerator.generate(40)) };
        preferences.db = String.format("jdbc:adb:%s", randomStringGenerator.generate(8));

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
