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
import java.util.List;
import javax.naming.ConfigurationException;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.db.api.BDDAssertions.then;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.Before;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import static ste.w3.easywallet.TestingConstants.WALLET1;
import static ste.w3.easywallet.TestingConstants.WALLET2;
import ste.w3.easywallet.data.TableSourceSorting;
import ste.w3.easywallet.data.Order;

/**
 *
 */
public class TransactionsManagerTest implements TestingUtils {

    private final Preferences preferences;

    public TransactionsManagerTest() throws Exception {
        preferences = bindPreferences();
    }

    @Before
    public void before() throws Exception {
        preferences.db = JDBC_CONNECTION_STRING;
        try (ConnectionSource db = new JdbcConnectionSource(preferences.db)) {
            TableUtils.dropTable(db, Transaction.class, true);
        }
    }

    @Test
    public void initialization_ok() throws Exception {
        TransactionsManager tm = new TransactionsManager();
        then(tm.transactions).isNotNull();
    }

    @Test
    public void initialization_with_errors_throws_ManagerException() throws Exception {
        preferences.db = null;

        try {
            TransactionsManager tm = new TransactionsManager();
            fail("bad initialization not detected");
        } catch (ManagerException x) {
            then(x)
                .hasMessage("error trying to access transactions data")
                .hasCauseInstanceOf(ConfigurationException.class);
        }

    }

    @Test
    public void getAll_returns_all_transactions() throws Exception {
        TransactionsManager tm = new TransactionsManager();

        then(tm.getAll()).isEmpty();
        tm.add(
            new Transaction(
                new Date(), STORJ, new BigDecimal("123.456"), "fromaddress", "toaddress", "newtransactionhash"
            )
        );
        then(tm.getAll()).hasSize(1).element(0).hasFieldOrPropertyWithValue("hash", "newtransactionhash");
    }

    @Test
    public void get_returns_selected_transactions() throws Exception {
        givenDatabase();

        TransactionsManager tm = new TransactionsManager();

        then(tm.get(null, 0, Integer.MAX_VALUE)).hasSize(37);

        then(tm.get(null, 0, 0)).isEmpty();

        List<Transaction> rows = tm.get(null, 0, 10);
        then(rows).hasSize(10);
        then(rows.get(0).hash).startsWith("hash000000001-");
        then(rows.get(9).hash).startsWith("hash000000010-");

        rows = tm.get(null, 10, 5l);
        then(rows).hasSize(5);
        then(rows.get(0).hash).startsWith("hash000000011-");
        then(rows.get(4).hash).startsWith("hash000000015-");

        for(TableSourceSorting s: new TableSourceSorting[] { new TableSourceSorting("hash", Order.NONE), null}) {
            rows = tm.get(s, 0, Integer.MAX_VALUE);
            int i = 0;
            for (Transaction t: tm.getAll()) {
                then(t.hash).isEqualTo(rows.get(i++).hash);
            }
        }

        //
        // NOTE: no nulls involved
        //
        then(
            tm.get(new TableSourceSorting("when", Order.ASCENDING), 0, Integer.MAX_VALUE)
        ).isSortedAccordingTo((t1, t2) -> t1.when.compareTo(t2.when));
        then(
            tm.get(new TableSourceSorting("when", Order.DESCENDING), 0, Integer.MAX_VALUE)
        ).isSortedAccordingTo((t1, t2) -> t2.when.compareTo(t1.when));
        then(
            tm.get(new TableSourceSorting("hash", Order.ASCENDING), 0, Integer.MAX_VALUE)
        ).isSortedAccordingTo((t1, t2) -> t1.hash.compareTo(t2.hash));
        then(
            tm.get(new TableSourceSorting("hash", Order.DESCENDING), 0, Integer.MAX_VALUE)
        ).isSortedAccordingTo((t1, t2) -> t2.hash.compareTo(t1.hash));
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

    @Test
    public void most_recent_transaction() throws Exception {
        final TransactionsManager tm = new TransactionsManager();

        then(tm.mostRecentTransaction()).isNull();
        givenDatabase();

        then(tm.mostRecentTransaction().when).isEqualTo(
            tm.get(new TableSourceSorting("when", Order.DESCENDING), 0, 1).get(0).when
        );

        tm.add(new Transaction(new Date(), null, BigDecimal.ZERO, WALLET1, WALLET2, "newtrhash"));
        then(tm.mostRecentTransaction().hash).isEqualTo("newtrhash");
    }

}
