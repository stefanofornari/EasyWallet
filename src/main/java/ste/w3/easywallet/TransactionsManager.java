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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import static okio.Okio.source;
import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class TransactionsManager {
    public final Dao<Transaction, String> transactions;

    public TransactionsManager() throws ManagerException {
        ConnectionSource db = null;
        try {
            Preferences preferences = (Preferences)new InitialContext().lookup("root/preferences");
            if (StringUtils.isBlank(preferences.db)) {
                throw new ConfigurationException("db connection string is missing");
            }
            db = new JdbcConnectionSource(preferences.db);
            transactions = DaoManager.createDao(db, Transaction.class);
            TableUtils.createTableIfNotExists(db, Transaction.class); // TODO: move in a better central place
        } catch (Exception x) {
            throw new ManagerException("error trying to access transactions data", x);
        } finally {
            if (db != null) {
                try { db.close(); } catch (Exception c) {}
            }
        }
    }

    public List<Transaction> allTransactions() throws ManagerException {
        try {
            return transactions.queryBuilder().orderBy("when", false).query();
        } catch (SQLException x) {
            throw new ManagerException("error retrieving transactions data", x);
        }
    }

    public void add(Transaction t) throws SQLException {
        transactions.create(t);
    }

}
