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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
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

/**
 *
 */
public class BaseLedgerManagerTest implements TestingUtils {

    public TestingServer server = null;

    protected static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    protected static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    protected static final Instant NOW = Instant.now();
    protected static final Transaction[] TRANSACTIONS1 = new Transaction[] {
        //
        // NOTE: these represents the transactions in a block; all shall have
        //       the same timestamp
        //
        new Transaction(new Date(NOW.toEpochMilli()), STORJ, BigDecimal.ONE, ADDRESS1, WALLET1, "block1-hash1", 1111),
        new Transaction(new Date(NOW.toEpochMilli()), GLM, BigDecimal.TWO, null, WALLET1, "block1-hash2", 1111),
        new Transaction(new Date(NOW.toEpochMilli()), GLM, BigDecimal.TEN, null, WALLET2, "block1-hash3", 1111),
        new Transaction(new Date(NOW.toEpochMilli()), null, BigDecimal.TEN, null, WALLET1, "block1-hash4", 1111),
        new Transaction(new Date(NOW.toEpochMilli()), new Coin("FAKE", "FAKE", "68c929e7b8fb06c58494a369f6f088fff28f7c77", 10), BigDecimal.TEN, null, WALLET1, "block1-hash5", 1111)
    };
    protected static final Transaction[] TRANSACTIONS2 = new Transaction[] {
        new Transaction(new Date(NOW.minus(24, ChronoUnit.HOURS).toEpochMilli()), STORJ, BigDecimal.ONE, null, WALLET1, "block2-hash1",1112),
        new Transaction(new Date(NOW.minus(24, ChronoUnit.HOURS).toEpochMilli()), STORJ, BigDecimal.ONE, null, WALLET1, "block2-hash1", 1112), // duplicated
        new Transaction(new Date(NOW.minus(24, ChronoUnit.HOURS).toEpochMilli()), STORJ, BigDecimal.valueOf(1, 50), null, WALLET1, "block2-hash1", 1112) // amount too big
    };
    protected static final Transaction[] TRANSACTIONS3 = new Transaction[] {
        new Transaction(new Date(NOW.minus(100, ChronoUnit.HOURS).toEpochMilli()), GLM, BigDecimal.TEN, null, WALLET1, "block3-hash1", 1113)
    };
    protected static final Transaction[] TRANSACTIONS4 = new Transaction[] {
        new Transaction(new Date(1641032490000l /*  Saturday 1 January 2022 10:21:30 GMT */), GLM, BigDecimal.TEN, null, WALLET1, "block4-hash1", 1114)
    };

    protected static final Wallet W1 = new Wallet(WALLET1);

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

    protected void givenTransfersBlock() {
        server.addLatestTransfersRequest(TRANSACTIONS1, COINS);
    }

    protected void givenTransfersBlocks() {
        server.addLatestTransfersRequest(TRANSACTIONS1, COINS); // block # 35545770
        server.addTransfersRequest(35545770, TRANSACTIONS2, COINS);
        server.addTransfersRequest(35545769,TRANSACTIONS3, COINS);
        server.addTransfersRequest(35545768, TRANSACTIONS4, COINS);
    }

    protected void givenMixedTransactionsBlock() throws IOException {
        server.addTransfersRequest(
            FileUtils.readFileToString(new File("src/test/examples/transactions-body-1.json"), "UTF8")
        );
    }


}
