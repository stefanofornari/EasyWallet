/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2022 Stefano Fornari. Licensed under the
 * EUPL-1.2 or later (see LICENSE).
 *
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
package ste.w3.easywallet;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.RandomStringGenerator;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static ste.w3.easywallet.TestingConstants.ETH;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;

/**
 * TODO: split in UI and DB
 */
public interface TestingUtils {
    final List<Transaction> transactions = new ArrayList<>();

    //public static final String JDBC_DRIVER_CLASS = "org.hsqldb.jdbc.JDBCDriver";
    public static final String JDBC_CONNECTION_STRING = "jdbc:hsqldb:mem:testdb";


    default public void showInStage(Stage stage, Pane pane) {
        //
        // We set the roor as opposed to create a Scene any time for
        // performance reasons
        //

        Scene s = stage.getScene();
        if (s == null) {
            stage.setScene(new Scene(pane));
        } else {
            s.setRoot(pane);
        }
        stage.show();
    }

    default public void showInStageLater(Stage stage, Pane pane) {
        Platform.runLater(() -> {
            showInStage(stage, pane);
        });
        waitForFxEvents();
    }

    default public <T> T getController(Pane pane) {
        return (T) pane.getUserData();
    }

    default Preferences givenEmptyPreferences() throws NamingException {
        Preferences preferences = new Preferences();

        Context ctx = new InitialContext();
        if (!ctx.list("").hasMore()) {
            ctx.createSubcontext("root");
        }
        ctx.rebind("root/preferences", preferences);

        return preferences;
    }

    default public Dao<Transaction, String> transactionsDao(ConnectionSource source)
    throws SQLException {
        return DaoManager.createDao(source, Transaction.class);
    }

    default String givenDatabase(final Wallet wallet, final int howMany) throws Exception {
        final String connectionString = getRandomConnectionString();
        transactions.clear();
        try (ConnectionSource db = new JdbcConnectionSource(connectionString)) {
            Dao<Transaction, String> transactionDao = DaoManager.createDao(db, Transaction.class);

            TableUtils.dropTable(transactionDao, true);
            TableUtils.createTable(transactionDao);

            Coin[] coins = new Coin[] { ETH, STORJ, GLM };
            Random r = new Random();
            for (int i=1; i<=howMany; ++i) {
                Transaction t = new Transaction(
                    new Date(Instant.parse(String.format("2022-11-%02dT10:00:00.00Z", r.nextInt(20)+1)).getEpochSecond()*1000),
                    coins[i%3],
                    new BigDecimal(String.format("%1$02d.%1$02d", r.nextInt(100))),
                    String.format("12345678901234567890123456789012345678%02d", r.nextInt(100)),
                    //
                    // If wallet is given, use its address as destination (to)
                    // any other row
                    //
                    ((wallet != null) && (i%2 == 0)) ?
                        wallet.address :
                        String.format("%02d12345678901234567890123456789012345678", r.nextInt(100)),
                    String.format("hash%09d-%02d", i, r.nextInt(80))
                );
                transactions.add(t);
                transactionDao.create(t);
            }
        }
        return connectionString;
    }

    default String givenDatabase(final Wallet wallet) throws Exception {
        return givenDatabase(wallet, 63);  // just a odd number to test with 10-20 items
    }

    default String givenDatabase(final int howMany) throws Exception {
        return givenDatabase(null, howMany);
    }

    default String givenDatabase() throws Exception {
        return givenDatabase(37);  // just a odd number to test with 10-20 items
    }

    default String givenEmptyDatabase() throws Exception {
        return givenDatabase(0);  // table is dropped and recreated, no rows added
    }

    default String getRandomConnectionString() {
        return String.format("%s-%08X", JDBC_CONNECTION_STRING, System.currentTimeMillis());
    }

    default Preferences givenPreferences(final File preferencesFile, final String endpoint) throws IOException {
        preferencesFile.getParentFile().mkdirs();

        //
        // Create some randomness to make sure the content is correctly read
        //
        RandomStringGenerator randomStringGenerator
                = new RandomStringGenerator.Builder()
                        .selectFrom("0123456789abcdef".toCharArray())
                        .build();
        Preferences preferences = new Preferences();
        preferences.endpoint = endpoint;
        preferences.appkey = randomStringGenerator.generate(12);
        preferences.wallets = new Wallet[]{new Wallet(randomStringGenerator.generate(40))};
        preferences.coins = new Coin[]{ETH, STORJ};
        preferences.db = getRandomConnectionString();

        PreferencesManager pm = new PreferencesManager();

        FileUtils.writeStringToFile(preferencesFile, pm.toJSON(preferences), "UTF-8");

        return preferences;
    }

    default TestingServer givenServer() {
        TestingServer server = new TestingServer();
        server.ethereum.start();
        return server;
    }

    default void givenRequests(final TestingServer server, final Preferences preferences) {
        server.addBalanceRequest(preferences.wallets[0].address, BigDecimal.ZERO);
        server.addBalanceRequest(STORJ, preferences.wallets[0].address, BigDecimal.ZERO);
        server.addLatestTransfersRequest(111111, new Transaction[0], new Coin[0]);
    }

}
