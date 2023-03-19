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
import static ste.xtest.Constants.BLANKS;

/**
 *
 */
public class ABIUtilsTest {

    public static final String INPUT1 = "0xa9059cbb00000000000000000000000014F2c84A58e065C846c5fDDdadE0d3548F97A5170000000000000000000000000000000000000000000000000102a7121cb5ce2a";
    public static final String INPUT2 = "0xa9059cbb00000000000000000000000055110C24859411822932a5263C6E44318cc3E6820000000000000000000000000000000000000000000000000c7230489e800000";
    public static final String INPUT3 = "0xbde0a2f7000000000000000000000000000000000000000000000000000000006415c7a30000000000000000000000001dd08307aee00258050775bcd3de5c7a7cac54cb";

    @Test
    public void sanity_checks_throw_IllegalArgumentException() throws Exception {
        final Transaction T = new Transaction();

        for (String blank: BLANKS) {
            try {
                new ABIUtils().tranferInputDecode(blank, T);
                fail("missing sanity check on input");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("input can not be null or empty");
            }
        }

        try {
            new ABIUtils().tranferInputDecode(INPUT1.substring(0, 50), T);
            fail("missing sanity check on input");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("input without 0x shall be of size " + (INPUT1.length()-2) + " (it was 48)");
        }

        try {
            new ABIUtils().tranferInputDecode(INPUT1, null);
            fail("missing sanity check on transaction");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("transaction can not be null");
        }
    }

    @Test
    public void transfer_decode() throws Exception {
        final Transaction T = new Transaction();

        new ABIUtils().tranferInputDecode(INPUT1, T);
        then(T.to).isEqualTo("14f2c84a58e065c846c5fdddade0d3548f97a517");
        then(T.amount).isEqualTo("0.07280424022427601");

        new ABIUtils().tranferInputDecode(INPUT2, T);
        then(T.to).isEqualTo("55110c24859411822932a5263c6e44318cc3e682");
        then(T.amount).isEqualTo("0.896832364255117312");
    }

    @Test
    public void transfer_with_wrong_method_throws_exception() {
        try {
            new ABIUtils().tranferInputDecode(INPUT3, new Transaction());
            fail("missing check for method");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("not incoming coin transaction (0xbde0a2f7)");
        }
    }


}
