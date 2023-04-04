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
import java.net.MalformedURLException;
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
import org.web3j.protocol.http.HttpService;
import ste.w3.easywallet.ABIUtils;
import ste.w3.easywallet.Block;
import ste.w3.easywallet.Coin;
import static ste.w3.easywallet.Coin.COIN_UNKOWN;
import static ste.w3.easywallet.Constants.EASYWALLET_LOG_NAME;
import static ste.w3.easywallet.Constants.LOG_BLOCK_FORMAT_OK;
import static ste.w3.easywallet.Constants.LOG_TRANSACTION_FORMAT_KO;
import static ste.w3.easywallet.Constants.LOG_TRANSACTION_FORMAT_OK;
import ste.w3.easywallet.ManagerException;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.TransactionsManager;
import static ste.w3.easywallet.Utils.ts;
import static ste.w3.easywallet.Utils.unex;
import ste.w3.easywallet.data.BlocksManager;

/**
 * TOTO: refactor to move commodity code in a base class for LedgerManager and
 * WalletManager
 */
public class LedgerManager {

    protected final Logger LOG = Logger.getLogger(EASYWALLET_LOG_NAME);

    private final Web3j web3;

    public final String endpoint;
    public final Map<String, Coin> coinMap = new HashMap<>();

    public LedgerManager(final String endpoint) {
        try {
            new URL(endpoint);
        } catch (MalformedURLException x) {
            throw new IllegalArgumentException(
                    String.format("endpoint is not a valid url (%s)", endpoint)
            );
        }
        this.endpoint = endpoint;
        web3 = Web3j.build(new HttpService(endpoint));

        try {
            if (coins() != null) {
                for (Coin c : coins()) {
                    if (c.contract != null) {
                        coinMap.put(c.contract.toLowerCase(), c);
                    }
                }
            }
        } catch (ConfigurationException x) {
            //
            // todo: log the error
            //
        }
    }

    public void refresh() throws ManagerException {
        boolean ok = true;
        //
        // TODO: break if block number not found?
        //
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("refresh start");
        }

        try {
            final TransactionsManager TM = new TransactionsManager();
            final BlocksManager BM = new BlocksManager();

            Block mostRecentBlock = BM.mostRecent(); // null if db is empty

            //
            // Retrieve the latest block and if the db is empty save it and take
            // it as the most recent
            //
            EthBlock.Block latestEthBlock = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
            if (mostRecentBlock == null) {
                mostRecentBlock = getBlock(latestEthBlock);
                saveEthBlock(BM, TM, latestEthBlock);
                return;
            } else if (mostRecentBlock.number.equals(latestEthBlock.getNumber())) {
                return;
            }

            for (
                BigInteger blockNumber = mostRecentBlock.number.add(BigInteger.ONE);
                latestEthBlock.getNumber().compareTo(blockNumber) > 0;
                blockNumber = blockNumber.add(BigInteger.ONE)
            ) {
                saveEthBlock(
                    BM, TM,
                    web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock()
                );
            }

            //
            // we still need to save the latest block gathered at the beginning
            //
            saveEthBlock(BM, TM, latestEthBlock);
        } catch (Exception x) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("refresh interrupted");
            }
            ok = false;
            // TODO: log the exception (maybe in the caller?)
            throw new ManagerException("error retrieving transfers", x);
        } finally {
            if (ok && LOG.isLoggable (Level.INFO)) {
                LOG.info("refresh done");
            }
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

    private void saveEthBlock(BlocksManager bm, TransactionsManager tm, EthBlock.Block ethBlock)
    throws ConfigurationException, ManagerException {
        final ABIUtils ABI = new ABIUtils();
        final List<EthBlock.TransactionResult> transactions = ethBlock.getTransactions();

        final Date when = new Date(ethBlock.getTimestamp().longValue() * 1000);

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(
                String.format(
                    LOG_BLOCK_FORMAT_OK,
                    ethBlock.getHash(),
                    String.valueOf(ethBlock.getNumber()),
                    ts(ethBlock.getTimestamp().longValue())
                )
            );
        }

        for (EthBlock.TransactionResult tr : transactions) {
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
                tm.add(transaction);
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
        } // for

        bm.add(getBlock(ethBlock));
    }

    private Block getBlock(final EthBlock.Block ethBlock)
    throws ManagerException {
        return new Block(
            new Date(ethBlock.getTimestamp().longValue() * 1000),
            ethBlock.getNumber(),
            unex(ethBlock.getHash())
        );
    }
}
