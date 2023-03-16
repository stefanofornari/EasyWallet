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

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import ste.w3.easywallet.ABIUtils;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.EasyWalletException;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.TransactionsManager;
import static ste.w3.easywallet.Utils.unex;

/**
 * TOTO: refactor to move commodity code in a base class for LedgerManager and
 * WalletManager
 */
public class LedgerManager {

    private final Web3j web3;

    public final String endpoint;

    public LedgerManager(final String endpoint) {
        try {
            new URL(endpoint);
        } catch (Exception x) {
            throw new IllegalArgumentException(
                String.format("endpoint is not a valid url (%s)", endpoint)
            );
        }
        this.endpoint = endpoint;
        web3 = Web3j.build(new HttpService(endpoint));
    }

    public void refresh() throws EasyWalletException {
        final ABIUtils ABI = new ABIUtils();

        try {
            final Coin[] COINS = coins();
            final TransactionsManager TM = new TransactionsManager();

            Block block = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
            List<EthBlock.TransactionResult> transactions = block.getTransactions();

            final Date when = new Date(block.getTimestamp().longValue());

            for(EthBlock.TransactionResult tr: transactions) {
                EthBlock.TransactionObject t = (EthBlock.TransactionObject) tr.get();

                Transaction transaction = new Transaction(
                    when,
                    null,
                    null,
                    unex(t.getFrom()),
                    unex(t.getTo()),
                    unex(t.getHash())
                );

                try {
                    ABI.transactionInputDecode(t.getInput(), transaction, COINS);
                    TM.add(transaction);
                } catch (IllegalArgumentException x) {
                    //
                    // TODO: Ignoring for now; this happens when the transaction is of
                    // different type than incoming token
                }
            }
        } catch (Exception x) {
            throw new EasyWalletException(x);
        }
    }

    // --------------------------------------------------------- private methods

    private Coin[] coins() throws ConfigurationException {
        try {
            Preferences preferences = (Preferences)new InitialContext().lookup("root/preferences");
            return preferences.coins;
        } catch (Exception x) {
            throw new ConfigurationException(x.getMessage());
        }

    }
}
