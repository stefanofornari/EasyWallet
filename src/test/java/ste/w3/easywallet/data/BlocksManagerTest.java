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
package ste.w3.easywallet.data;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import ste.w3.easywallet.Block;
import ste.w3.easywallet.ManagerException;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingUtils;
import wiremock.org.apache.commons.lang3.time.DateUtils;

/**
 *
 */
public class BlocksManagerTest implements TestingUtils {

    private final Preferences preferences;

    public BlocksManagerTest() throws Exception {
        preferences = givenEmptyPreferences();
    }

    @Before
    public void before() throws Exception {
        preferences.db = getRandomConnectionString();
        try (ConnectionSource db = new JdbcConnectionSource(preferences.db)) {
            TableUtils.dropTable(db, Block.class, true);
        }
    }

    @Test
    public void initialization_ok() throws Exception {
        BlocksManager tm = new BlocksManager();
        then(tm.blocks).isNotNull();
    }

    @Test
    public void initialization_with_errors_throws_ManagerException() throws Exception {
        preferences.db = null;

        try {
            BlocksManager tm = new BlocksManager();
            fail("bad initialization not detected");
        } catch (ManagerException x) {
            then(x)
                .hasMessageContaining("error trying to access transactions data")
                .hasCauseInstanceOf(ConfigurationException.class);
        }

    }

    @Test
    public void mostRecent_returns_null_if_empty_db() throws Exception {
        BlocksManager tm = new BlocksManager();
        then(tm.mostRecent()).isNull();
    }

    @Test
    public void mostRecent_returns_the_most_recent_block() throws Exception {
        final Date TS = new Date(1648887012000l);

        BlocksManager tm = new BlocksManager();

        //
        // given some blocks (inserted unordered on purpose)
        //
        tm.add(new Block(DateUtils.addDays(TS, 2), BigInteger.TWO, "hash2"));
        tm.add(new Block(DateUtils.addDays(TS, 0), BigInteger.ONE, "hash1"));
        then(tm.mostRecent().hash).isEqualTo("hash2");

        //
        // add a most recent block
        //
        tm.add(new Block(DateUtils.addDays(TS, 3), BigInteger.TEN, "hash3"));
        then(tm.mostRecent().hash).isEqualTo("hash3");

        //
        // add a not most recent block
        //
        tm.add(new Block(DateUtils.addDays(TS, 0), BigInteger.TEN, "hash4"));
        then(tm.mostRecent().hash).isEqualTo("hash3");
    }

    @Test
    public void mostRecent_error() throws Exception {
        final BlocksManager TM = new BlocksManager();
        TableUtils.dropTable(TM.blocks, true);  // let's cause an SQL Exception

        try {
            TM.mostRecent();
            fail("exception not thrown");
        } catch (ManagerException x) {
            then(x).hasMessageStartingWith("error retriving most recent block").hasCauseInstanceOf(SQLException.class);
        }
    }

    @Test
    public void all_returns_all_transactions() throws Exception {
        final BlocksManager BM = new BlocksManager();

        then(BM.all()).isEmpty();
        BM.add(
            new Block(
                new Date(), new BigInteger("123456"), "newtransactionhash"
            )
        );
        then(BM.all()).hasSize(1).element(0).hasFieldOrPropertyWithValue("hash", "newtransactionhash");
        BM.add(
            new Block(
                new Date(), new BigInteger("7890123"), "anothertransactionhash"
            )
        );
        List<Block> all = BM.all();
        then(all).hasSize(2);
        then(all).element(1).hasFieldOrPropertyWithValue("hash", "newtransactionhash");
        then(all).element(0).hasFieldOrPropertyWithValue("hash", "anothertransactionhash");
    }

    @Test
    public void all_error() throws Exception {
        final BlocksManager TM = new BlocksManager();
        TableUtils.dropTable(TM.blocks, true);  // let's cause an SQL Exception

        try {
            TM.all();
            fail("exception not thrown");
        } catch (ManagerException x) {
            then(x).hasMessageStartingWith("error retrieving blocks data").hasCauseInstanceOf(SQLException.class);
        }
    }

    /*
    @Test
    public void get_returns_selected_transactions() throws Exception {
        preferences.db = givenDatabase();

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

        for(TableSou    rceSorting s: new TableSourceSorting[] { new TableSourceSorting("hash", Order.NONE), null}) {
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
    */

    @Test
    public void add_adds_a_transaction_to_the_database() throws Exception {
        BlocksManager tm = new BlocksManager();

        Date d = new Date();
        tm.add(new Block(d, new BigInteger("123456"), "hash1"));

        Table table = new Table(new Source(preferences.db, "sa", ""), "blocks");
        then(table).hasNumberOfRows(1).row(0)
            .value().isEqualTo("hash1")
            .value().isEqualTo(new BigInteger("123456"))
            .value().isEqualTo(new Timestamp(d.getTime()))
            ;
    }

    @Test
    public void add_error() throws Exception {
        final BlocksManager TM = new BlocksManager();
        TableUtils.dropTable(TM.blocks, true);  // let's cause an SQL Exception

        try {
            TM.add(new Block(new Date(), new BigInteger("123456"), "hash1"));
            fail("exception not thrown");
        } catch (ManagerException x) {
            then(x).hasMessageStartingWith("error adding a block").hasCauseInstanceOf(SQLException.class);
        }
    }

}
