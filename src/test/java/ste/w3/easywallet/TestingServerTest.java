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

import static org.assertj.core.api.BDDAssertions.entry;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.ADDRESS1;

/**
 *
 */
public class TestingServerTest implements TestingConstants {

    @Test
    public void add_balance() {
        TestingServer server = new TestingServer();

        //
        // default values
        //
        then(server.TEST_BALANCE).containsExactly(
                entry(ex(ADDRESS1), "0x7baa706cf4a4220055045"),
                entry(ex(ADDRESS2), "0x1bf7395fc44bec91e8000")
        );

        server.addBalance(ex(ADDRESS3), "0x00");
        then(server.TEST_BALANCE).containsExactly(
                entry(ex(ADDRESS1), "0x7baa706cf4a4220055045"),
                entry(ex(ADDRESS2), "0x1bf7395fc44bec91e8000"),
                entry(ex(ADDRESS3), "0x00")
        );

        server.addBalance(ex(ADDRESS4), "0x0011");
        then(server.TEST_BALANCE).containsExactly(
                entry(ex(ADDRESS1), "0x7baa706cf4a4220055045"),
                entry(ex(ADDRESS2), "0x1bf7395fc44bec91e8000"),
                entry(ex(ADDRESS3), "0x00"),
                entry(ex(ADDRESS4), "0x0011")
        );
    }

    @Test
    public void add_balance_fails_if_arguments_are_not_hex() {
        TestingServer server = new TestingServer();
        try {
            server.addBalance(WALLET1, "0x00");
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("address must start with '0x'");
        }
        try {
            server.addBalance(ex(ADDRESS1), "0");
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("balance must start with '0x'");
        }
    }

    // --------------------------------------------------------- private methods

    private String ex(final String s) {
        return "0x" + s;
    }

}
