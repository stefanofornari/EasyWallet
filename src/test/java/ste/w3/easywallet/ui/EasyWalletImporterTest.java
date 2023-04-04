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
package ste.w3.easywallet.ui;

import java.io.File;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingServer;
import ste.w3.easywallet.TestingUtils;
import ste.w3.easywallet.TransactionsManager;
import ste.w3.easywallet.data.BlocksManager;
import static ste.w3.easywallet.ledger.BaseLedgerManagerTest.TRANSACTIONS1;
import static ste.w3.easywallet.ui.BaseEasyWalletMain.CONFIG_FILE;

/**
 *
 */
public class EasyWalletImporterTest implements TestingUtils {

    @Rule
    public TemporaryFolder HOME = new TemporaryFolder();

    @Test
    public void launch_transfers_importer() throws Exception {
        TestingServer server = givenServer();
        Preferences preferences = givenPreferences(
            new File(HOME.getRoot(), CONFIG_FILE),
            server.ethereum.url("v3/apikey-" + System.currentTimeMillis())
        );
        server.addLatestTransfersRequest(111222, TRANSACTIONS1, new Coin[0]);

        final EasyWalletImporter EWL = new EasyWalletImporterWithConfigFile();

        EWL.main();

        final BlocksManager BM = new BlocksManager();
        final TransactionsManager TM = new TransactionsManager();

        then(BM.all()).hasSize(1);
        then(TM.all()).hasSize(5);

    }

    // ---

    private class EasyWalletImporterWithConfigFile extends EasyWalletImporter {

        @Override
        protected File getConfigFile() {
            return new File(HOME.getRoot(), CONFIG_FILE);
        }
    }

}
