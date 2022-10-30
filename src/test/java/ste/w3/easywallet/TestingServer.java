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

import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 *
 * @author ste
 */
public class TestingServer implements TestingConstants {

    public final MockWebServer ethereum;

    public final Map<String, String> TEST_BALANCE = new HashMap<>();

    public TestingServer() {
        TEST_BALANCE.put("0x" + ADDRESS1, "0x7baa706cf4a4220055045");
        TEST_BALANCE.put("0x" + ADDRESS2, "0x1bf7395fc44bec91e8000");
        ethereum = new MockWebServer();
        //ethereum.setDispatcher(dispatcher());
    }

    /**
     * Add a balance entry to <code>TEST_BALANCE</code>
     *
     * @param address prefixed by 0x
     * @param balance as hex (prefixed by 0x)
     */
    public void addBalance(String address, String balance) {
        if (!address.startsWith("0x")) {
            throw new IllegalArgumentException("address must start with '0x'");
        }
        if (!balance.startsWith("0x")) {
            throw new IllegalArgumentException("balance must start with '0x'");
        }
        TEST_BALANCE.put(address, balance);
    }
    /*
    08:44:03.386 [main] DEBUG org.web3j.protocol.http.HttpService - vary: Origin
08:44:03.388 [main] DEBUG org.web3j.protocol.http.HttpService -
08:44:03.388 [main] DEBUG org.web3j.protocol.http.HttpService - {"jsonrpc":"2.0","id":3,"result":"0x0000000000000000000000000000000000000000000000005a850fa96456afd7"}
08:44:03.388 [main] DEBUG org.web3j.protocol.http.HttpService - <-- END HTTP (102-byte body)
symbol: GLM
name: Golem Network Token (PoS)
decimal: 18
balance (0x3eAE5d25Aa262a8821357f8b03545d9a6eB1D9F2)=6.522636855523323863 (6522636855523323863)
    */

    public void addBalanceRequest(Coin coin, String address, BigDecimal balance) {
        BigDecimal bigBalance = balance.movePointRight(coin.decimals);

        String body = String.format("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x%064x\"}", bigBalance.toBigInteger());

        MockResponse r = new MockResponse().setBody(body)
            .setHeader("content-type", "application/json")
            .setHeader("content-length", String.valueOf(body.length()));

        ethereum.enqueue(r);
    }

    // --------------------------------------------------------- private methods

    private Dispatcher dispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest r) throws InterruptedException {
                Gson g = new Gson();

                String content = r.getBody().readUtf8();

                System.out.println(">>>");
                System.out.println(r.getRequestUrl());
                System.out.println(content);
                System.out.println("<<<");

                HashMap body = g.fromJson(content, HashMap.class);
                String address = (String)((List)body.get("params")).get(0);

                return new MockResponse().setHeader("content-type", "application/json")
                    .setBody(String.format(
                        "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"%s\"}", TEST_BALANCE.get(address)
                    )
                );
            }

        };
    }

}
