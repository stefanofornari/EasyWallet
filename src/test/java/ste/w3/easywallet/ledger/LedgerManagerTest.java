/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2023 Stefano Fornari. Licensed under the
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
package ste.w3.easywallet.ledger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import static ste.w3.easywallet.TestingConstants.COINS;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import static ste.w3.easywallet.TestingConstants.WALLET1;
import static ste.w3.easywallet.TestingConstants.WALLET2;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.TestingUtils;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;
import static ste.xtest.Constants.BLANKS;

/**
 * TODO: move common code with WalletmanagerTest in a base class
 */
public class LedgerManagerTest implements TestingUtils {

    public TestingServer server = null;

    private static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    private static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    private static final Instant NOW = Instant.now();
    private static final Transaction[] TRANSACTIONS1 = new Transaction[] {
        new Transaction(new Date(NOW.toEpochMilli()), STORJ, BigDecimal.ONE, null, WALLET1, "hash1"),
        new Transaction(new Date(NOW.toEpochMilli()+3600*1000), GLM, BigDecimal.TWO, null, WALLET1, "hash2"),
        new Transaction(new Date(NOW.toEpochMilli()+2*3600*1000), GLM, BigDecimal.TEN, null, WALLET2, "hash3"),
        new Transaction(new Date(NOW.toEpochMilli()+3*3600*1000), null, BigDecimal.TEN, null, WALLET1, "hash4"),
        new Transaction(new Date(NOW.toEpochMilli()+4*3600*1000), new Coin("FAKE", "FAKE", "68c929e7b8fb06c58494a369f6f088fff28f7c77", 10), BigDecimal.TEN, null, WALLET1, "hash5")
    };

    private static final Wallet W1 = new Wallet(WALLET1);

    @Before
    public void before() throws Exception {
        Preferences preferences = givenEmptyPreferences();
        givenEmptyDatabase();
        server = new TestingServer();
        server.ethereum.start();
        preferences.db = JDBC_CONNECTION_STRING;
        preferences.coins = COINS;
    }

    @After
    public void after() throws Exception {
        server.ethereum.stop();
    }

    @Test
    public void construct_ledger_manager() {
        LedgerManager lm = new LedgerManager("https://mainnet.infura.io/v3/PROJECTID1/"+ TEST_APP_KEY_1);
        then(lm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID1/" + TEST_APP_KEY_1);

        lm = new LedgerManager("https://mainnet.infura.io/v3/PROJECTID2/" + TEST_APP_KEY_2);
        then(lm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID2/" + TEST_APP_KEY_2);

        List<String> invalid_endpoints = new ArrayList();
        invalid_endpoints.addAll(Arrays.asList(BLANKS));
        invalid_endpoints.add("nourl");
        for(String e: invalid_endpoints) {
            try {
                new LedgerManager(e);
                fail("missing sanity check for endpoint");
            } catch (IllegalArgumentException x) {
                then(x).hasMessageContaining("endpoint is not a valid url (" + e + ")");
            }
        }
    }

    @Test
    public void refresh_updates_the_database_with_latest_block() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));
        final LedgerSource LS = new LedgerSource(W1);

        givenIncomingCoinTransactionsBlock();

        LM.refresh(); LS.fetch();

        then(LS.page).hasSize(4);
        then(LS.page.get(0).coin).isEqualTo(STORJ.symbol);
        then(LS.page.get(1).coin).isEqualTo(GLM.symbol);
        then(LS.page.get(2).coin).isEqualTo("UNKNOWN");
        then(LS.page.get(3).coin).isEqualTo("UNKNOWN");
    }

    @Test
    public void refresh_ignores_not_incoming_coin_transactions() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));
        final LedgerSource LS = new LedgerSource(W1);

        givenMixedTransactionsBlock();

        LM.refresh(); LS.fetch();

        then(LS.page).hasSize(2);
    }

    @Test
    public void refresh_does_not_load_the_block_twice () {
        fail("tbd");
    }


    @Test
    public void ignore_not_incoming_coins_transactions() {
        fail("tbd");
    }

    @Test
    public void throw_an_application_exception_in_case_of_errors() {
        fail("tbd");
    }

    // --------------------------------------------------------- private methods

    private void givenIncomingCoinTransactionsBlock() {
        server.addTransfersRequest(TRANSACTIONS1, COINS);
    }

    private void givenMixedTransactionsBlock() throws IOException {
        server.addTransfersRequest(
            FileUtils.readFileToString(new File("src/test/examples/transactions-body-1.json"), "UTF8")
        );
    }


}
