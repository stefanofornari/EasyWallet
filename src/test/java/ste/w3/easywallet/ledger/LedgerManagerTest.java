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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import ste.w3.easywallet.ManagerException;
import static ste.w3.easywallet.TestingConstants.COINS;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import ste.w3.easywallet.Transaction;
import static ste.xtest.Constants.BLANKS;

/**
 *
 */
public class LedgerManagerTest extends BaseLedgerManager {

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
    public void refresh_updates_the_db_with_latest_blocks_up_to_6_months_old() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));
        final LedgerSource LS = new LedgerSource(W1);

        givenTransfersBlocks();

        LM.refresh(); LS.fetch();

        then(LS.page).hasSize(6);
        then(LS.page.get(0).hash).isEqualTo("block1-hash1");
        then(LS.page.get(0).coin).isEqualTo(STORJ.symbol);
        then(LS.page.get(1).hash).isEqualTo("block1-hash2");
        then(LS.page.get(1).coin).isEqualTo(GLM.symbol);
        then(LS.page.get(2).hash).isEqualTo("block1-hash4");
        then(LS.page.get(2).coin).isEqualTo("UNKNOWN");
        then(LS.page.get(3).hash).isEqualTo("block1-hash5");
        then(LS.page.get(3).coin).isEqualTo("UNKNOWN");
        then(LS.page.get(4).hash).isEqualTo("block2-hash1");
        then(LS.page.get(4).coin).isEqualTo(STORJ.symbol);
        then(LS.page.get(5).hash).isEqualTo("block3-hash1");
        then(LS.page.get(5).coin).isEqualTo(GLM.symbol);
    }

    @Test
    public void refresh_ignores_not_transfers() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));
        final LedgerSource LS = new LedgerSource(W1);

        givenMixedTransactionsBlock();

        try {
            LM.refresh();
        } catch (ManagerException x) {
            //
            // we are supposed to get an execption because we made available one
            // block only
            //
        }
        LS.fetch();

        then(LS.page).hasSize(2);
        //
        // Let's check we loaded the correct transactions
        //
        Transaction t = LS.page.get(0);
        then(t.hash).isEqualTo("hash1");
        then(t.when).isEqualTo(new Date(1668324371000l));
        then(t.from).isEqualTo("f6a01c044dedc636f5f93f14bde8a53b4212d0b3");
        then(t.to).isEqualTo("1234567890123456789012345678901234567890");
        then(String.valueOf(t.amount)).isEqualTo("1E-18");
        then(t.coin).isEqualTo("UNKNOWN");

        t = LS.page.get(1);
        then(t.hash).isEqualTo("hash2");
        then(t.when).isEqualTo(new Date(1668324371000l));
        then(t.from).isEqualTo("bba01c044dedc6f6f5f93f14bde8a53b4212d032");
        then(t.to).isEqualTo("1234567890123456789012345678901234567890");
        then(String.valueOf(t.amount)).isEqualTo("2E-18");
        then(t.coin).isEqualTo("UNKNOWN");
    }

    @Test
    public void refresh_does_not_load_the_block_twice () throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));
        final LedgerSource LS = new LedgerSource(W1);

        givenTransfersBlock();

        try {
            LM.refresh(); // error if trying to insert twice the same transaction
        } catch (ManagerException x) {
            //
            // we are supposed to get an execption because we made available one
            // block only
            //
        }
        LS.fetch();
        then(LS.page).hasSize(4);
    }

    @Test
    public void throw_an_application_exception_in_case_of_errors() {
        server.addFailure();
        try {
            new LedgerManager(server.ethereum.url("fake")).refresh();
            fail("no application exception");
        } catch (ManagerException x) {
            then(x).hasMessageContaining("error retrieving transfers");
        }

        server.addError(404, "not found");
        try {
            new LedgerManager(server.ethereum.url("fake")).refresh();
            fail("no application exception");
        } catch (ManagerException x) {
            then(x).hasMessageContaining("error retrieving transfers");
        }
    }

}
