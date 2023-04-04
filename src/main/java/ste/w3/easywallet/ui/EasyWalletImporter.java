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
import java.io.IOException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.commons.io.FileUtils;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.WalletManager;
import ste.w3.easywallet.ledger.LedgerManager;

/**
 *
 */
class EasyWalletImporter {

    public final File configFile = getConfigFile();

    protected Preferences preferences = null;
    protected WalletManager walletManager = null;
    protected LedgerManager ledgerManager = null;

    public void main() throws Exception {
        initialize();

        ledgerManager.refresh();
    }

    public static void main(String... args) throws Exception {
        new EasyWalletImporter().main();
    }

    protected void initialize() throws IOException, NamingException {
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

}
