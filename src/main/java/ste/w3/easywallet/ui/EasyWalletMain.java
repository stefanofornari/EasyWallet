package ste.w3.easywallet.ui;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;

/**
 * Preferences are stored in a file under $(CONFIG_HOME)/ste.w3.easywallet/preferences.json
 *
 * $(CONFIG_HOME) is usually $(HOME)/.config
 */
public class EasyWalletMain extends Application {

    public final File configFile = getConfigFile();

    private Preferences preferences = new Preferences();

    @Override
    public void start(Stage stage) {
        PreferencesManager pm = new PreferencesManager();
        try {
            preferences = pm.fromJSON(FileUtils.readFileToString(configFile, "UTF-8"));
        } catch (IOException x) {
            x.printStackTrace();
        }

        stage.setTitle("EasyWallet v0.1");
        stage.setWidth(575);
        stage.setHeight(800);

        Scene scene = new Scene(
            new EasyWalletFXMLLoader().loadMainWindow(preferences.wallets)
        );

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public Preferences getPreferences() {
        return preferences;
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
