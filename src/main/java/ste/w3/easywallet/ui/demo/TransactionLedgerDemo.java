package ste.w3.easywallet.ui.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.io.IOException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import javafx.scene.layout.Pane;
import javax.naming.Context;
import javax.naming.InitialContext;
//import org.scenicview.ScenicView;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.ui.EasyWalletFXMLLoader;
import ste.w3.easywallet.ui.LedgerController;

/**
 * NOTES:
 * <ul>
 *  <li>only one column for now can be selected for sorting; this is because we can
 *   not (yet) tell which column should be used first (ordering by column 1 and
 *   then column 2 is different that ordering by column 2 first and then by
 *   column 1.</li>
 * </ul>
 */
public class TransactionLedgerDemo extends Application {

    private static final Wallet WALLET = new Wallet("1489a7dd02ca2294ed999cfc175050c852851dec");
    private static final String TITLE = String.format("0x%s's incoming token transfers", "1489a7dd02ca2294ed999cfc175050c852851dec");

    @Override
    public void start(Stage stage) throws Exception {
        //Preferences preferences = bindPreferences();
        Preferences preferences = new Preferences();

        Context ctx = new InitialContext();
        ctx.createSubcontext("root");
        ctx.bind("root/preferences", preferences);
        preferences.coins = new Coin[] { new Coin("STORJ", 8), new Coin("GLM", 12) };

        Pane main = (Pane)new EasyWalletFXMLLoader().loadLedgerDialogContent(new Wallet("01d34567890123456789012345678901234567890"), null);

        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();

        final LedgerController controller = (LedgerController)main.getUserData();

        //
        // Just for demo purposes, let's set a small page size
        //
        controller.source().pageSize(20);

        controller.fetch();
//        ScenicView.show(scene);  // to use it, enable the dependency in pom.xml
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TransactionLedgerDemo.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws Exception {
        try (ConnectionSource db = new JdbcConnectionSource("jdbc:hsqldb:mem:testdb")) {
            Dao<Transaction, String> transactionDao = DaoManager.createDao(db, Transaction.class);

            TableUtils.createTable(db, Transaction.class);

            Coin[] coins = new Coin[] { new Coin("ETH", 12), new Coin("STORJ", 8), new Coin( "GLM", 12) };
            for (int i=1; i<=25; ++i) {
                transactionDao.create(
                    new Transaction(
                        new Date(Instant.parse(String.format("2022-11-10T10:%02d:00.00Z", i)).getEpochSecond()*1000),
                        coins[i%3],
                        new BigDecimal(String.format("%1$02d.%1$02d", i)),
                        String.format("12345678901234567890123456789012345678%02d",i),
                        String.format("%02d34567890123456789012345678901234567890",i%2),
                        String.format("hahs%02d",i)
                    )
                );
            }
        }

        launch();
    }

}
