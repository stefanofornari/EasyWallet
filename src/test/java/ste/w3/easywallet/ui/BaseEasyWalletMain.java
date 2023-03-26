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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;
import ste.xtest.concurrent.WaitFor;

/**
 *
 */
public class BaseEasyWalletMain extends ApplicationTest implements TestingConstants, TestingUtils {

    public TestingServer server = null;

    protected EasyWalletMainWithPreferences main;
    protected Preferences preferences;
    protected Stage stage;
    protected EasyWalletMainController controller;

    protected static final String CONFIG_FILE = ".config/ste.w3.easywallet/preferences.json";

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("-----> " + description.getMethodName());
        }
    };

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        givenServer();
        givenPreferences();
        givenMainWindow();

        main.start(stage);
        controller = getController(lookup("#main").queryAs(Pane.class));
    }

    @Override
    public void stop() {
        server.ethereum.stop();
    }

    protected File getPreferencesFile() throws IOException {
        return new File(HOME.getRoot(), CONFIG_FILE);
    }

    protected Preferences givenPreferences() throws IOException {
        File preferencesFile = getPreferencesFile();

        preferencesFile.getParentFile().mkdirs();

        //
        // Create some randomness to make sure the content is correctly read
        //
        RandomStringGenerator randomStringGenerator
                = new RandomStringGenerator.Builder()
                        .selectFrom("0123456789abcdef".toCharArray())
                        .build();
        preferences = new Preferences();
        preferences.endpoint = server.ethereum.url("v3/" + randomStringGenerator.generate(20)).toString();
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[]{new Wallet(randomStringGenerator.generate(40))};
        preferences.coins = new Coin[]{ETH, STORJ};
        preferences.db = getRandomConnectionString();

        PreferencesManager pm = new PreferencesManager();

        FileUtils.writeStringToFile(preferencesFile, pm.toJSON(preferences), "UTF-8");

        return preferences;
    }

    protected EasyWalletMainWithPreferences givenMainWindow() {
        return (main = new EasyWalletMainWithPreferences());
    }

    protected TestingServer givenServer() {
        server = new TestingServer();
        server.ethereum.start();
        return server;
    }

    protected void withConnectionException() throws Exception {
        server.addFailure();
    }

    protected void givenRequests() {
        server.addBalanceRequest(preferences.wallets[0].address, BigDecimal.ZERO);
        server.addBalanceRequest(STORJ, preferences.wallets[0].address, BigDecimal.ZERO);
        server.addTransfersRequest(new Transaction[0], new Coin[0]);
    }

    protected void waitForRefresh() {
        new WaitFor(5000, () -> !controller.refreshButton.isDisabled());
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
    }

}
