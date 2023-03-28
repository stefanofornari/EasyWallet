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

import java.math.BigInteger;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import ste.w3.easywallet.ABIUtils;
import ste.w3.easywallet.Coin;
import static ste.w3.easywallet.Coin.COIN_UNKOWN;
import static ste.w3.easywallet.Constants.EASYWALLET_LOG_NAME;
import static ste.w3.easywallet.Constants.LOG_BLOCK_FORMAT_KO;
import static ste.w3.easywallet.Constants.LOG_BLOCK_FORMAT_OK;
import static ste.w3.easywallet.Constants.LOG_TRANSACTION_FORMAT_KO;
import static ste.w3.easywallet.Constants.LOG_TRANSACTION_FORMAT_OK;
import ste.w3.easywallet.ManagerException;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.TransactionsManager;
import static ste.w3.easywallet.Utils.ts;
import static ste.w3.easywallet.Utils.unex;

/**
 * TOTO: refactor to move commodity code in a base class for LedgerManager and
 * WalletManager
 */
public class LedgerManager {

    protected final Logger LOG = Logger.getLogger(EASYWALLET_LOG_NAME);

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

    public void refresh() throws ManagerException {
        final Date SIX_MONTHS_AGO = new Date(System.currentTimeMillis()-6*30*24*60*60*1000l);
        final ABIUtils ABI = new ABIUtils();
        //
        // TODO: break if block number not found
        //

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("refresh start");
        }

        try {
            final TransactionsManager TM = new TransactionsManager();
            final Transaction mostRecentTransaction = TM.mostRecentTransaction();

            final Map<String, Coin> coinMap = new HashMap<>();
            if (coins() != null) {
                for (Coin c: coins()) {
                    if (c.contract != null) {
                        coinMap.put(c.contract.toLowerCase(), c);
                    }
                }
            }

            BigInteger nextBlockNumber = null;
            for (long i=0; i<500000; ++i) {  // just to make sure we do not loop forever
                DefaultBlockParameter blockNumberParameter = (nextBlockNumber == null)
                                                           ? DefaultBlockParameterName.LATEST
                                                           : DefaultBlockParameter.valueOf(nextBlockNumber);

                Block block = web3.ethGetBlockByNumber(blockNumberParameter, true).send().getBlock();

                List<EthBlock.TransactionResult> transactions = block.getTransactions();

                final Date when = new Date(block.getTimestamp().longValue()*1000);
                if (when.before(SIX_MONTHS_AGO)) {
                    LOG.info(
                        String.format(
                            LOG_BLOCK_FORMAT_KO,
                            block.getHash(),
                            String.valueOf(block.getNumber()),
                            ts(block.getTimestamp().longValue()),
                            ts(SIX_MONTHS_AGO)
                        )
                    );
                    break;
                }

                if (LOG.isLoggable(Level.INFO)) {
                    LOG.info(
                        String.format(
                            LOG_BLOCK_FORMAT_OK,
                            block.getHash(),
                            String.valueOf(block.getNumber()),
                            ts(block.getTimestamp().longValue())
                        )
                    );
                }

                if ((mostRecentTransaction != null) && (!when.after(mostRecentTransaction.when))) {
                    break;
                }

                for(EthBlock.TransactionResult tr: transactions) {
                    EthBlock.TransactionObject t = (EthBlock.TransactionObject) tr.get();

                    Transaction transaction = new Transaction(
                        when,
                        (t.getTo() != null) ? coinMap.getOrDefault(unex(t.getTo().toLowerCase()), COIN_UNKOWN) : COIN_UNKOWN,
                        null,
                        unex(t.getFrom()),
                        null,
                        unex(t.getHash())
                    );

                    try {
                        ABI.tranferInputDecode(t.getInput(), transaction);
                        TM.add(transaction);
                    } catch (IllegalArgumentException x) {
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.info(
                                String.format(
                                    LOG_TRANSACTION_FORMAT_KO,
                                    transaction.hash,
                                    "not an incoming transfer"
                                )
                            );
                        }
                    } catch (SQLException x) {
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.info(
                                String.format(
                                    LOG_TRANSACTION_FORMAT_KO,
                                    transaction.hash,
                                    x.getMessage()
                                )
                            );
                        }
                    }

                    if (LOG.isLoggable(Level.INFO)) {
                        LOG.info(
                            String.format(
                                LOG_TRANSACTION_FORMAT_OK,
                                transaction.hash,
                                transaction.from,
                                transaction.to,
                                transaction.coin,
                                String.valueOf(transaction.amount)
                            )
                        );
                    }
                }
                nextBlockNumber = block.getNumber().subtract(BigInteger.ONE);
            }
        } catch (Exception x) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("refresh interrupted");
            }
            // TODO: log the exception (maybe in the caller?)
            throw new ManagerException("error retrieving transfers", x);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("refresh done");
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
