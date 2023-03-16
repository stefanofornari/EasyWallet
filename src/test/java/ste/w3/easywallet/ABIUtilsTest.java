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

import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.COINS;
import static ste.w3.easywallet.TestingConstants.ETH;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;
import static ste.xtest.Constants.BLANKS;

/**
 *
 */
public class ABIUtilsTest {

    public static final String INPUT1 = "0xa9059cbb00000000000000000000000014F2c84A58e065C846c5fDDdadE0d3548F97A5170000000000000000000000000000000000000000000000000102a7121cb5ce2a";
    public static final String INPUT2 = "0xa22cb46500000000000000000000000055110C24859411822932a5263C6E44318cc3E6820000000000000000000000000000000000000000000000000c7230489e800000";

    @Test
    public void sanity_checks_throw_IllegalArgumentException() throws Exception {
        final Transaction T = new Transaction();

        for (String blank: BLANKS) {
            try {
                new ABIUtils().transactionInputDecode(blank, T, new Coin[0]);
                fail("missing sanity check on input");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("input can not be null or empty");
            }
        }

        try {
            new ABIUtils().transactionInputDecode(INPUT1.substring(0, 50), T, new Coin[0]);
            fail("missing sanity check on input");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("input shall be of size " + INPUT1.length() + " (it was 50)");
        }

        try {
            new ABIUtils().transactionInputDecode(INPUT1, null, new Coin[0]);
            fail("missing sanity check on transaction");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("transaction can not be null");
        }
    }

    @Test
    public void transaction_input_decode() throws Exception {
        final Transaction T = new Transaction();

        new ABIUtils().transactionInputDecode(INPUT1, T, COINS);
        then(T.coin).isEqualTo(STORJ.symbol);
        then(T.amount).isEqualTo("0.07280424022427601");

        new ABIUtils().transactionInputDecode(INPUT2, T, COINS);
        then(T.coin).isEqualTo(GLM.symbol);
        then(T.amount).isEqualTo("0.896832364255117312");
    }

    @Test
    public void decode_with_unknown_coins() {
        final Transaction T = new Transaction();

        new ABIUtils().transactionInputDecode(INPUT1, T, null);
        then(T.coin).isEqualTo("UNKNOWN");
        new ABIUtils().transactionInputDecode(INPUT1, T, new Coin[] {GLM});
        then(T.coin).isEqualTo("UNKNOWN");
        new ABIUtils().transactionInputDecode(INPUT2, T, new Coin[] {ETH}); // main coin
        then(T.coin).isEqualTo("UNKNOWN"); // the main coin is not a token
    }

}
