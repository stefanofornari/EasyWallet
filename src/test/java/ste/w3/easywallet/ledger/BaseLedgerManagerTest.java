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
import java.math.BigInteger;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import ste.w3.easywallet.Block;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import static ste.w3.easywallet.TestingConstants.ADDRESS1;
import static ste.w3.easywallet.TestingConstants.COINS;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import static ste.w3.easywallet.TestingConstants.WALLET1;
import static ste.w3.easywallet.TestingConstants.WALLET2;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.TestingUtils;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.data.BlocksManager;
import wiremock.org.apache.commons.lang3.time.DateUtils;

/**
 *
 */
public class BaseLedgerManagerTest implements TestingUtils {

    public TestingServer server = null;

    protected static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    protected static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    protected static final Date TS = new Date(1648887012000l);
    public static final Transaction[] TRANSACTIONS1 = new Transaction[] {
        //
        // NOTE: these represents the transactions in a block; all shall have
        //       the same timestamp
        //
        new Transaction(TS, STORJ, BigDecimal.ONE, ADDRESS1, WALLET1, "block1-hash1"),
        new Transaction(TS, GLM, BigDecimal.TWO, null, WALLET1, "block1-hash2"),
        new Transaction(TS, GLM, BigDecimal.TEN, null, WALLET2, "block1-hash3"),
        new Transaction(TS, null, BigDecimal.TEN, null, WALLET1, "block1-hash4"),
        new Transaction(TS, new Coin("FAKE", "FAKE", "68c929e7b8fb06c58494a369f6f088fff28f7c77", 10), BigDecimal.TEN, null, WALLET1, "block1-hash5")
    };
    public static final Transaction[] TRANSACTIONS2 = new Transaction[] {
        new Transaction(DateUtils.addDays(TS, 1), STORJ, BigDecimal.ONE, null, WALLET1, "block2-hash1"),
        new Transaction(DateUtils.addDays(TS, -1), STORJ, BigDecimal.ONE, null, WALLET1, "block2-hash1"), // duplicated
        new Transaction(DateUtils.addDays(TS, -1), STORJ, BigDecimal.valueOf(1, 50), null, WALLET1, "block2-hash1") // amount too big
    };
    public static final Transaction[] TRANSACTIONS3 = new Transaction[] {
        new Transaction(DateUtils.addHours(TS, -100), GLM, BigDecimal.TEN, null, WALLET1, "block3-hash1")
    };
    public static final Transaction[] TRANSACTIONS4 = new Transaction[] {
        new Transaction(new Date(1641032490000l /*  Saturday 1 January 2022 10:21:30 GMT */), GLM, BigDecimal.TEN, null, WALLET1, "block4-hash1")
    };

    protected static final Wallet W1 = new Wallet(WALLET1);
    protected static final Wallet W2 = new Wallet(WALLET2);

    @Before
    public void before() throws Exception {
        Preferences preferences = givenEmptyPreferences();
        preferences.db = givenEmptyDatabase();
        server = new TestingServer();
        server.ethereum.start();
        preferences.coins = COINS;
    }

    @After
    public void after() throws Exception {
        server.ethereum.stop();
    }

    protected void givenLatestTransfersBlock() {
        server.addLatestTransfersRequest(111111, TRANSACTIONS1, COINS);
    }

    protected void givenTransfersBlocks() {
        server.addLatestTransfersRequest(111111, TRANSACTIONS1, COINS);
        server.addTransfersRequest(111110, TRANSACTIONS2, COINS);
        server.addTransfersRequest(111109,TRANSACTIONS3, COINS);
        server.addTransfersRequest(111108, TRANSACTIONS4, COINS);
    }

    protected void givenMixedTransactionsBlock() throws IOException {
        server.addTransfersRequest(
            FileUtils.readFileToString(new File("src/test/examples/transactions-body-1.json"), "UTF8")
        );
    }

    protected void givenDBWithBlocks(long... blocks) throws Exception {
        final Date TS = new Date(1648880803);
        final BlocksManager BM = new BlocksManager();
        int d = 0;
        for(long b: blocks) {
            BM.add(new Block(DateUtils.addDays(TS, d++), BigInteger.valueOf(b), "hash-" + b));
        }
    }


}
