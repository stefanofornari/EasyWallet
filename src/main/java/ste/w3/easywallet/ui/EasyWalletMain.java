package ste.w3.easywallet.ui;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 * Preferences are stored in a file under $(CONFIG_HOME)/ste.w3.easywallet/preferences.json
 *
 * $(CONFIG_HOME) is usually $(HOME)/.config
 */
public class EasyWalletMain extends Application {

    public final File configFile = getConfigFile();

    private Preferences preferences = new Preferences();
    private WalletManager walletManager = null;

    @Override
    public void start(Stage stage) {
        PreferencesManager pm = new PreferencesManager();
        try {
            preferences = pm.fromJSON(FileUtils.readFileToString(configFile, "UTF-8"));
        } catch (IOException x) {
            x.printStackTrace();
        }

        walletManager = new WalletManager(preferences.url());

        stage.setTitle("EasyWallet v0.1");
        stage.setWidth(575);
        stage.setHeight(800);
        stage.getIcons().add(new Image(EasyWalletMain.class.getResourceAsStream("/images/easy-wallet-icon-64x64.png")));
        stage.getIcons().add(new Image(EasyWalletMain.class.getResourceAsStream("/images/easy-wallet-icon-128x128.png")));

        Scene scene = new Scene(
            new EasyWalletFXMLLoader().loadMainWindow(this)
        );

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm());

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Add a wallet by adding it to the preferences and save the updated
     * preferences on disk.
     *
     * @param wallet the wallet to add
     */
    public void addWallet(Wallet wallet) {
        Wallet[] newList = new Wallet[preferences.wallets.length+1];
        System.arraycopy(preferences.wallets, 0, newList, 0, preferences.wallets.length);
        newList[preferences.wallets.length] = wallet;
        preferences.wallets = newList;
        savePreferences();
    }

    public void deleteWallet(Wallet wallet) {
        Wallet[] newList = new Wallet[preferences.wallets.length-1];
        int i=0;
        for(Wallet w: preferences.wallets) {
            if (!w.address.equals(wallet.address)) {
                newList[i++] = w;
            }
        }
        preferences.wallets = newList;
        savePreferences();
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public void savePreferences() {
        try {
            PreferencesManager pm = new PreferencesManager();
            FileUtils.write(getConfigFile(), pm.toJSON(preferences), "UTF-8");
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ------------------------------------------------------- protected methods

    protected File getConfigFile() {
        return new File(
            FileUtils.getUserDirectory(), ".config/ste.w3.easywallet/preferences.json"
        );
    }

    // --------------------------------------------------------- private method

}
