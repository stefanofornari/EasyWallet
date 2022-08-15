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
        TEST_BALANCE.put(ADDRESS1, "0x7baa706cf4a4220055045");
        TEST_BALANCE.put(ADDRESS2, "0x1bf7395fc44bec91e8000");
        ethereum = new MockWebServer();
        ethereum.setDispatcher(dispatcher());
    }

    // --------------------------------------------------------- private methods

    private Dispatcher dispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest r) throws InterruptedException {
                Gson g = new Gson();

                HashMap body = g.fromJson(r.getBody().readUtf8(), HashMap.class);
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
