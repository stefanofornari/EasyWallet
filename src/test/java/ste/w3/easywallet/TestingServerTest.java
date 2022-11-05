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

import java.math.BigDecimal;
import java.net.URL;
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

    @Test
    public void add_balance_request_enqueues_a_request() throws Exception {
        final String TEST1 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x000000000000000000000000000000000000000000000000000000011a2f36c1\"}";
        final String TEST2 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x0000000000000000000000000000000000000000000000005a850fa96456afd7\"}";
        TestingServer server = new TestingServer();
        server.addBalanceRequest(STORJ, "0x" + ADDRESS1, new BigDecimal("47.34269121"));
        server.addBalanceRequest(GLM, "0x" + ADDRESS1, new BigDecimal("6.522636855523323863"));
        server.ethereum.start();

        try {
            URL url = new URL(server.ethereum.url("something").toString());
            then(url.openConnection().getInputStream()).hasContent(TEST1);
            then(url.openConnection().getInputStream()).hasContent(TEST2);
        } finally {
            server.ethereum.shutdown();
        }
    }

    // --------------------------------------------------------- private methods

    private String ex(final String s) {
        return "0x" + s;
    }

}
