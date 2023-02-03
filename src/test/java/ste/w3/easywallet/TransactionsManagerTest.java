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

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.db.api.BDDAssertions.then;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.Before;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;

/**
 *
 */
public class TransactionsManagerTest implements TestingUtils {

    @Before
    public void before() throws Exception {
        ConnectionSource s = bindDatabase();
        TableUtils.dropTable(s, Transaction.class, true);
    }

    @Test
    public void initialization_ok() throws Exception {
        TransactionsManager tm = new TransactionsManager();
        then(tm.source).isNotNull().isInstanceOf(JdbcConnectionSource.class);
        then(tm.transactions).isNotNull();
    }

    @Test
    public void initialization_with_errors_throws_ManagerException() throws Exception {
        new InitialContext().destroySubcontext("root");

        try {
            TransactionsManager tm = new TransactionsManager();
            fail("bad initialization not detected");
        } catch (ManagerException x) {
            then(x)
                .hasMessage("error trying to access transactions data")
                .hasCauseInstanceOf(NamingException.class);
        }

    }

    @Test
    public void allTransactions_returns_all_transactions() throws Exception {
        TransactionsManager tm = new TransactionsManager();

        then(tm.allTransactions()).isEmpty();
        tm.add(
            new Transaction(
                new Date(), STORJ, new BigDecimal("123.456"), "fromaddress", "toaddress", "newtransactionhash"
            )
        );
        then(tm.allTransactions()).hasSize(1).element(0).hasFieldOrPropertyWithValue("hash", "newtransactionhash");
    }

    @Test
    public void add_adds_a_transaction_to_the_database() throws Exception {
        TransactionsManager tm = new TransactionsManager();

        Date d = new Date();
        tm.add(
            new Transaction(
                d, STORJ, new BigDecimal("123.456"), "fromaddress",  "toaddress", "transactionhash"
            )
        );

        Table table = new Table(new Source(JDBC_CONNECTION_STRING, "sa", ""), "transactions");
        then(table).hasNumberOfRows(1).row(0)
            .value().isEqualTo("transactionhash")
            .value().isEqualTo("fromaddress")
            .value().isEqualTo("toaddress")
            .value().isEqualTo(new BigDecimal("123.456")) // turn into BigDecimal
            .value().isEqualTo(new Timestamp(d.getTime()))
            .value().isEqualTo("STORJ");

        d = new Date(d.toInstant().plus(1, ChronoUnit.DAYS).getEpochSecond()*1000);
        tm.add(
            new Transaction(
                d, GLM, new BigDecimal("789.012"), "newfromaddress",  "newtoaddress", "newtransactionhash"
            )
        );

        table = new Table(new Source(JDBC_CONNECTION_STRING, "sa", ""),  "transactions");
        then(table).hasNumberOfRows(2).row(0)
            .value().isEqualTo("newtransactionhash")
            .value().isEqualTo("newfromaddress")
            .value().isEqualTo("newtoaddress")
            .value().isEqualTo(new BigDecimal("789.012"))
            .value().isEqualTo(new Timestamp(d.getTime()))
            .value().isEqualTo("GLM");
    }

}
