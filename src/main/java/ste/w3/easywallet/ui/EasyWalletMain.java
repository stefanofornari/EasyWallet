package ste.w3.easywallet.ui;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    private Preferences preferences = null;

    @Override
    public void start(Stage stage) {
        try {
            PreferencesManager pm = new PreferencesManager();
            preferences = pm.fromJSON(FileUtils.readFileToString(configFile, "UTF-8"));

            stage.setMinWidth(400);
            stage.setMinHeight(600);

            //
            // TODO: Move the window creation code into EasyWalletWindow and add a controller
            //
            Pane content = FXMLLoader.load(EasyWalletMain.class.getResource("/fxml/EasyWalletMain.fxml"));
            StackPane pane = (StackPane)content.getChildren().get(0);
            pane.getChildren().add(0, new EasyWalletWindow(preferences.wallets));

            Scene scene = new Scene(content);

            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm());

            stage.setTitle("EasyWallet v0.1");

            stage.setScene(scene);
            stage.show();
        } catch (IOException x) {
            x.printStackTrace();
        }

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

    // --------------------------------------------------------- private methods

}
