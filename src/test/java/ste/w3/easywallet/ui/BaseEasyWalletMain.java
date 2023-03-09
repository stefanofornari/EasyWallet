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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.EasyWalletException;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class BaseEasyWalletMain extends ApplicationTest implements TestingConstants, TestingUtils {

    public static TestingServer server = null;

    protected EasyWalletMain main;
    protected Preferences preferences;
    protected Stage stage;
    protected EasyWalletMainController controller;

    protected static final String CONFIG_FILE = ".config/ste.w3.easywallet/preferences.json";

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        server = new TestingServer(); server.ethereum.start();

        try {
            preparePreferences();
        } catch (IOException x) {
            x.printStackTrace();
        }

        main = new EasyWalletMainWithPreferences(); main.start(stage);

        controller = getController(lookup("#main").queryAs(Pane.class));
    }

    @Override
    public void stop() {
        server.ethereum.stop();
    }


    protected File getPreferencesFile() throws IOException {
        return new File(HOME.getRoot(), CONFIG_FILE);
    }

    protected void preparePreferences() throws IOException {
        File preferencesFile = getPreferencesFile();

        preferencesFile.getParentFile().mkdirs();

        //
        // Create some randomness to make sure the content is correctly read
        //
        RandomStringGenerator randomStringGenerator =
            new RandomStringGenerator.Builder()
                    .selectFrom("0123456789abcdef".toCharArray())
                    .build();
        preferences = new Preferences();
        preferences.endpoint = server.ethereum.url("v3/" + randomStringGenerator.generate(20)).toString();
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[] { new Wallet(randomStringGenerator.generate(40)) };
        preferences.coins = new Coin[] {ETH, STORJ};

        PreferencesManager pm = new PreferencesManager();

        FileUtils.writeStringToFile(preferencesFile, pm.toJSON(preferences), "UTF-8");
    }

    protected void withConnectionException() throws Exception {
        PrivateAccess.setInstanceValue(main, "walletManager", new WalletManager("http://somewere.com/key") {
            @Override
            public WalletManager balance(Wallet wallet, Coin... coins) throws EasyWalletException {
                throw new EasyWalletException("network not available");
            }

        });
    }


    // ------------------------------------------- EasyWalletMainWithPreferences

    protected class EasyWalletMainWithPreferences extends EasyWalletMain {

        @Override
        protected File getConfigFile() {
            try {
                return getPreferencesFile();
            } catch (IOException x) {
                x.printStackTrace();
            }

            return null;
        }

        @Override
        //
        // With this we make sure multiple instances do not interefeer each
        // other. The root can then be retrieved with the following code:
        //
        protected Context getJNDIRoot() throws NamingException {
            return new InitialContext().createSubcontext(String.valueOf(this.hashCode()));
        }
    }

}
