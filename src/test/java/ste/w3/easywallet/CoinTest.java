/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2022 Stefano Fornari. Licensed under the
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

import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.xtest.Constants.BLANKS;
import static ste.xtest.Constants.BLANKS_WITHOUT_NULL;
import static ste.xtest.Constants.NEGATIVES_1_25_389_4567;

/**
 *
 */
public class CoinTest {

    @Test
    public void constructor_ok() {
        Coin c = new Coin("ETH", "Ether", 18);
        then(c.contract).isNull();
        then(c.symbol).isEqualTo("ETH");
        then(c.name).isEqualTo("Ether");
        then(c.decimals).isEqualTo(18);

        c = new Coin("STORJ", "StorjToken", "0x1234567890abcdefghijklmnopqrtuwyzABCDEFG", 8);
        then(c.contract).isEqualTo("0x1234567890abcdefghijklmnopqrtuwyzABCDEFG");
        then(c.symbol).isEqualTo("STORJ");
        then(c.name).isEqualTo("StorjToken");
        then(c.decimals).isEqualTo(8);

        c = new Coin("MYCOIN", 10);
        then(c.contract).isNull();
        then(c.symbol).isEqualTo("MYCOIN");
        then(c.name).isEqualTo("MYCOIN");
        then(c.decimals).isEqualTo(10);
    }

    @Test
    public void constructor_ko() {
        for (String s: BLANKS) {
            try {
                new Coin(s, "abc", null, 10);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("symbol can not be blank");
            }
        }

        for (String s: BLANKS) {
            try {
                new Coin("abc", s, null, 10);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("name can not be blank");
            }
        }

        for (String s: BLANKS_WITHOUT_NULL) {
            try {
                new Coin("abc", "cde", s, 10);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("contract can not be blank");
            }
        }

        for (int i: NEGATIVES_1_25_389_4567) {
            try {
                new Coin("abc", "cde", "efg", i);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("decimals can not be less than 1");
            }
        }

    }
}
