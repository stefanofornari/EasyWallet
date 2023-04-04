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

import java.util.List;
import java.util.logging.Logger;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.Constants.EASYWALLET_LOG_NAME;
import ste.w3.easywallet.ManagerException;
import ste.xtest.logging.ListLogHandler;

/**
 *
 */
public class LedgerManager_loggingTest extends BaseLedgerManagerTest {

    private final Logger LOG = Logger.getLogger(EASYWALLET_LOG_NAME);
    private final ListLogHandler HANDLER = new ListLogHandler();


    public LedgerManager_loggingTest() {
        LOG.addHandler(HANDLER);
    }

    @Test
    public void log_block_and_transaction_loading() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));

        givenTransfersBlocks(); givenDBWithBlocks(111109);

        LM.refresh();

        final List<String> MESSAGES = HANDLER.getMessages();
        then(MESSAGES)
            .contains("refresh start")
            .contains("block 0xhash00000000000000000000000000000000000000000000000000000001B206 111110 20220403081012UTC ok")
            .contains("block 0xhash00000000000000000000000000000000000000000000000000000001B207 111111 20220402081012UTC ok")
            .contains("refresh done");

        then(MESSAGES)
            .contains("transaction 0xblock1-hash1 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 STORJ 1E-18")
            .contains("transaction 0xblock2-hash1 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 STORJ 1E-18")
            .contains("transaction 0xblock1-hash1 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 STORJ 1E-18")
            .contains("transaction 0xblock1-hash2 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 GLM 2E-18")
            .contains("transaction 0xblock1-hash3 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x0123456789012345678901234567890123456789 GLM 1E-17")
            .contains("transaction 0xblock1-hash4 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 UNKNOWN 1E-17")
            .contains("transaction 0xblock1-hash5 ok 0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3 0x1234567890123456789012345678901234567890 UNKNOWN 1E-17")
        ;
        then(MESSAGES.get(3)).contains("transaction 0xblock2-hash1 ko Unable to run insert stmt");
    }

    @Test
    public void log_not_incoming_transfers() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));

        givenMixedTransactionsBlock();
        try {
            LM.refresh();
        } catch (ManagerException x) {
            //
            // we are supposed to get an execption because we made available one
            // block only
            //
        }

        final List<String> MESSAGES = HANDLER.getMessages();
        then(MESSAGES).contains("transaction 0xhash3 ko not an incoming transfer");
    }

    @Test
    public void log_not_refresh_error() throws Exception {
        final LedgerManager LM = new LedgerManager(server.ethereum.url("fake"));

        givenMixedTransactionsBlock(); givenDBWithBlocks(111100);
        try {
            LM.refresh();
        } catch (ManagerException x) {
            //
            // we are supposed to get an execption because we made available one
            // block only
            //
        }

        then(HANDLER.getMessages()).contains("refresh interrupted").doesNotContain("refresh done");
    }
}
