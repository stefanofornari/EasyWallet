package ste.w3.easywallet.ui;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
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
        new Thread(() -> {
            try {
                SVGGlyphLoader.loadGlyphsFont(EasyWalletMain.class.getResourceAsStream("/fonts/icomoon.svg"), "icomoon.svg");
            } catch (IOException x) {
                x.printStackTrace();
            }
        }).start();

        try {
            PreferencesManager pm = new PreferencesManager();
            preferences = pm.fromJSON(FileUtils.readFileToString(configFile, "UTF-8"));

            stage.setWidth(400); stage.setHeight(600);
            stage.setMinHeight(400);

            //
            // TODO: Move the window creation code into EasyWalletWindow and add a controller
            //
            Pane content = FXMLLoader.load(EasyWalletMain.class.getResource("/fxml/EasyWalletMain.fxml"));
            StackPane pane = (StackPane)content.getChildren().get(0);
            pane.getChildren().add(0, new EasyWalletWindow(preferences.wallets));

            JFXDecorator decorator = new JFXDecorator(stage, content, false, true, true);
            Scene scene = new Scene(decorator);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
                               JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
                               EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm());

            decorator.setCustomMaximize(true);
            decorator.setGraphic(new SVGGlyph(""));

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
