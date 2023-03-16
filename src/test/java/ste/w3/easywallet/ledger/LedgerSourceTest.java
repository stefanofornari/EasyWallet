package ste.w3.easywallet.ledger;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingConstants;
import static ste.w3.easywallet.TestingConstants.ADDRESS3;
import ste.w3.easywallet.TestingUtils;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class LedgerSourceTest implements TestingUtils, TestingConstants {


    private static final Wallet WALLET = new Wallet(ADDRESS3);

    @Before
    public void before() throws Exception {
        Preferences p = givenEmptyPreferences();
        givenDatabase(WALLET, 100);

        p.db = JDBC_CONNECTION_STRING;
    }

    @Test
    public void set_and_get_page_size() {
        LedgerSource s = new LedgerSource(WALLET);

        //
        // wallet
        //
        then(s.wallet).isSameAs(WALLET);

        //
        // default value
        //
        then(s.pageSize()).isEqualTo(1000);

        //
        // set and get a new page size
        //
        s.pageSize(500);
        then(s.pageSize()).isEqualTo(500);

        //
        // illegal arguments
        //
        for(int i: new int[] {-100, -1, 0}) {
            try {
                s.pageSize(i);
                fail("no sanity check for pageSize");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("pageSize must be greater than 0");
                then(s.pageSize).isEqualTo(500);
            }
        }
    }

    @Test
    public void fetch_up_to_pageSize_rows() {
        LedgerSource s = new LedgerSource(WALLET);
        s.pageSize(45);

        s.fetch();
        then(s.page).hasSize(45);

        s.fetch();
        then(s.page).hasSize(50);
    }

    @Test
    public void fetch_wallet_transactions_only() {
        final LedgerSource s = new LedgerSource(WALLET);

        s.pageSize(1000);
        s.fetch();
        then(s.page).hasSize(50).allMatch(
            (transaction) -> WALLET.address.equalsIgnoreCase(transaction.to)
        );
    }
}
