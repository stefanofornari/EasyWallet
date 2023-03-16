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
package ste.w3.easywallet;

import ste.w3.easywallet.data.TableSourceSorting;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import org.apache.commons.lang3.StringUtils;
import ste.w3.easywallet.data.Order;

/**
 *
 */
public class TransactionsManager {
    public final Dao<Transaction, String> transactions;

    public TransactionsManager() throws ManagerException {
        try (ConnectionSource db = db()) {
            transactions = DaoManager.createDao(db, Transaction.class);
            TableUtils.createTableIfNotExists(db, Transaction.class);
        } catch (Exception x) {
            throw new ManagerException("error trying to access transactions data", x);
        }
    }

    public List<Transaction> getAll() throws ManagerException {
        try {
            return transactions.queryBuilder().query();
        } catch (SQLException x) {
            throw new ManagerException("error retrieving transactions data", x);
        }
    }

    /**
     * Returns a subset of (or all) rows sorted (or not) by the sorting argument.
     *
     * @param wallet the target wallet of the transactions to retrieve or null to retrieve all
     * @param sorting name of the column and sorting direction in a TableSourceSorting
     * @param startFrom position of the first row to return (0 based)
     * @param howMany number of rows to return or null for all; use a value big enough
     *                to return the entire dataset; please note that some databases
     *                do not allow to provide an offset without a limit (e.g. hsql)
     *
     * @return the results set
     *
     * @throws ManagerException
     */
    public List<Transaction> get(Wallet wallet, TableSourceSorting sorting, long startFrom, long howMany)
    throws ManagerException {
        //
        // See https://github.com/j256/ormlite-core/issues/281
        //
        if (howMany == 0) {
            return new ArrayList<>();
        }
        try {
            //
            // NOTE: at least with hsql, if offset is spedified, limit must be
            // specified as well
            //
            QueryBuilder qb = transactions.queryBuilder().offset(startFrom).limit(howMany);
            if (wallet != null) {
                qb.where().eq("to", wallet.address);
            }
            if ((sorting != null) && (sorting.order() != Order.NONE)) {
                qb.orderBy(sorting.column(), sorting.order() == Order.ASCENDING);
            }

            return qb.query();
        } catch (SQLException x) {
            throw new ManagerException("error retrieving transactions data", x);
        }
    }

    public List<Transaction> get(TableSourceSorting sorting, long startFrom, long howMany)
    throws ManagerException {
        return get(null, sorting, startFrom, howMany);
    }


    public void add(Transaction t) throws SQLException {
        transactions.create(t);
    }

    // --------------------------------------------------------- private methods

    private JdbcConnectionSource db() throws ConfigurationException {
        JdbcConnectionSource db = null;

        try {
            Preferences preferences = (Preferences)new InitialContext().lookup("root/preferences");
            if (StringUtils.isBlank(preferences.db)) {
                throw new ConfigurationException("db connection string is missing");
            }
            db = new JdbcConnectionSource(preferences.db);
        } catch (Exception x) {
            x.printStackTrace();
            throw new ConfigurationException("db connection string is missing");
        }

        return db;
    }

}
