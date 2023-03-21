package ste.w3.easywallet.ui;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.commons.io.FileUtils;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;
import ste.w3.easywallet.ledger.LedgerManager;

/**
 * Preferences are stored in a file under $(CONFIG_HOME)/ste.w3.easywallet/preferences.json
 *
 * $(CONFIG_HOME) is usually $(HOME)/.config
 */
public class EasyWalletMain extends Application {

    public final File configFile = getConfigFile();

    private Preferences preferences = null;
    private WalletManager walletManager = null;
    private LedgerManager ledgerManager = null;

    @Override
    public void start(Stage stage) throws Exception {
        //
        // TODO: handle exceptions and remove throws Exception (naming, io, sql)
        initialize();
        // ///

        stage.setTitle("EasyWallet v0.1");
        stage.setWidth(575);
        stage.setHeight(800);
        stage.getIcons().add(new Image(EasyWalletMain.class.getResourceAsStream("/images/easy-wallet-icon-64x64.png")));
        stage.getIcons().add(new Image(EasyWalletMain.class.getResourceAsStream("/images/easy-wallet-icon-128x128.png")));

        Scene scene = new Scene(
            new EasyWalletFXMLLoader().loadMainWindow(this)
        );

        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(EasyWalletMain.class.getResource("/css/EasyWallet.css").toExternalForm());

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

    // TODO: rename to preferences()
    public Preferences getPreferences() {
        return preferences;
    }

    // TODO: rename to walletManager()
    public WalletManager getWalletManager() {
        return walletManager;
    }

    public LedgerManager ledgerManager() {
        return ledgerManager;
    }

    public void savePreferences() {
        try {
            PreferencesManager pm = new PreferencesManager();
            FileUtils.write(getConfigFile(), pm.toJSON(preferences), "UTF-8");
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    //
    // TODO: cleanup database code
    //
    public static void main(String[] args) throws Exception {
        try (ConnectionSource db = new JdbcConnectionSource("jdbc:hsqldb:mem:testdb")) {
            Dao<Transaction, String> transactionDao = DaoManager.createDao(db, Transaction.class);

            TableUtils.createTable(db, Transaction.class);

            Coin[] coins = new Coin[] {
                new Coin("ETH", "Ethereum", 18),
                new Coin("STORJ", "STORJ", 12),
                new Coin("GLM", "GLM", 18)
            };

            for (int i=1; i<=25; ++i) {
                transactionDao.create(
                        new Transaction(
                                new Date(Instant.parse(String.format("2022-11-10T10:%02d:00.00Z", i)).getEpochSecond()*1000),
                                coins[i%3],
                                new BigDecimal(String.format("%1$02d.%1$02d", i)),
                                String.format("12345678901234567890123456789012345678%02d",i),
                                String.format("%02d12345678901234567890123456789012345678",i),
                                String.format("hahs%02d",i)
                        )
                );
            }

            //ctx.bind("root/db", db);
        }

        launch(args);
    }

    protected void initialize() throws Exception {
        Context ctx = getJNDIRoot();

        PreferencesManager pm = new PreferencesManager();
        preferences = pm.fromJSON(FileUtils.readFileToString(configFile, "UTF-8"));
        ctx.rebind("preferences", preferences);

        walletManager = new WalletManager(preferences.url());
        ledgerManager = new LedgerManager(preferences.url());
    }

    // ------------------------------------------------------- protected methods

    protected File getConfigFile() {
        return new File(
            FileUtils.getUserDirectory(), ".config/ste.w3.easywallet/preferences.json"
        );
    }

    protected Context getJNDIRoot() throws NamingException {
        InitialContext initialContext = new InitialContext();
        try {
            return (Context)initialContext.lookup("root");
        } catch (NameNotFoundException x) {
            return initialContext.createSubcontext("root");
        }
    }

    // --------------------------------------------------------- private method

}
