package ste.w3.easywallet.ledger;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import ste.w3.easywallet.TestingUtils;

/**
 *
 */
public class LedgerSourceTest implements TestingUtils {

    @Before
    public void before() throws Exception {
        givenDatabase(100);
    }

    @Test
    public void set_and_get_page_size() {
        //
        // default value
        //
        LedgerSource s = new LedgerSource();
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
    public void fetch_up_tp_pageSize_rows() {
        LedgerSource s = new LedgerSource();
        s.pageSize(75);

        s.fetch();
        then(s.page).hasSize(75);

        s.fetch();
        then(s.page).hasSize(100);
    }


}
