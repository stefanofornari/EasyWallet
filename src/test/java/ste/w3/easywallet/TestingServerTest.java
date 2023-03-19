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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.time.Instant;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.ADDRESS1;
import static ste.w3.easywallet.TestingServer.BALANCE_REQUEST_FORMAT;
import static ste.w3.easywallet.TestingServer.LATEST_TRANSACTIONS_REQUEST;
import static ste.w3.easywallet.TestingServer.LATEST_TRANSACTION_RESPONSE_FORMAT;
import static ste.w3.easywallet.TestingServer.TOKEN_BALANCE_REQUEST_FORMAT;


/**
 *
 */
public class TestingServerTest implements TestingConstants {
    private TestingServer server = new TestingServer();
    private OkHttpClient http = new OkHttpClient();

    @Before
    public void before() {
        server.ethereum.start();
    }

    @After
    public void after() {
        server.ethereum.stop();
    }

    @Test
    public void server_initialization() {
        then(server.ethereum).isNotNull();
    }

    @Test
    public void add_token_balance_request_mocks_a_request() throws Exception {
        final String TEST1 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x000000000000000000000000000000000000000000000000000000011a2f36c1\"}";
        final String TEST2 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x0000000000000000000000000000000000000000000000005a850fa96456afd7\"}";

        server.addBalanceRequest(STORJ, ADDRESS1, new BigDecimal("47.34269121"));
        server.addBalanceRequest(GLM, ADDRESS1, new BigDecimal("6.522636855523323863"));

        Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                TOKEN_BALANCE_REQUEST_FORMAT, STORJ.contract, ADDRESS1),
                MediaType.parse("application/json")
            )
        ).build();
        Response response = http.newCall(request).execute();

        then(response.body().string()).isEqualTo(TEST1);

        request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                TOKEN_BALANCE_REQUEST_FORMAT, GLM.contract, ADDRESS1),
                MediaType.parse("application/json")
            )
        ).build();
        response = http.newCall(request).execute();
        then(response.body().string()).isEqualTo(TEST2);
    }

    @Test
    public void add_balance_request_mocks_a_request() throws Exception {
        final String TEST1 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x000019d82cd840\"}";
        final String TEST2 = "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x02513af93e8b40\"}";

        server.addBalanceRequest(ADDRESS1, new BigDecimal("0.00111001"));
        server.addBalanceRequest(ADDRESS2, new BigDecimal("6.52263685"));

        Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                BALANCE_REQUEST_FORMAT, ADDRESS1),
                MediaType.parse("application/json")
            )
        ).build();
        Response response = http.newCall(request).execute();

        then(response.body().string()).isEqualTo(TEST1);

        request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                BALANCE_REQUEST_FORMAT, ADDRESS2, GLM.contract),
                MediaType.parse("application/json")
            )
        ).build();
        response = http.newCall(request).execute();
        then(response.body().string()).isEqualTo(TEST2);
    }

    @Test
    public void add_transfers_request_mocks_a_request() throws Exception {
        final Instant NOW = Instant.now();
        final Transaction[] TRANSACTIONS1 = new Transaction[] {
            new Transaction(new Date(NOW.toEpochMilli()), STORJ, BigDecimal.ONE, null, WALLET1, "hash1"),
            new Transaction(new Date(NOW.toEpochMilli()+3600*1000), GLM, BigDecimal.TWO, null, WALLET1, "hash2"),
            new Transaction(new Date(NOW.toEpochMilli()+2*3600*1000), GLM, BigDecimal.TEN, null, WALLET2, "hash3"),
            new Transaction(new Date(NOW.toEpochMilli()+3*3600*1000), null, BigDecimal.TEN, null, WALLET1, "hash4"),
        };
        final Transaction[] TRANSACTIONS2 = new Transaction[] {
            new Transaction(new Date(NOW.toEpochMilli()), GLM, BigDecimal.valueOf(10343526), null, WALLET2, "hash4"),
        };

        server.addTransfersRequest(TRANSACTIONS1, COINS);

        final Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(
                LATEST_TRANSACTIONS_REQUEST,
                MediaType.parse("application/json")
            )
        ).build();

        Response response = http.newCall(request).execute();
        then(response.body().string()).isEqualTo(
            givenTransactionsBody("transactions-1.json")
        );

        server.addTransfersRequest(TRANSACTIONS2, COINS);

        response = http.newCall(request).execute();
        then(response.body().string()).isEqualTo(
            givenTransactionsBody("transactions-2.json")
        );
    }

    @Test
    public void add_transactions_body_request_mocks_a_request() throws Exception {
        final String TRANSACTIONS = FileUtils.readFileToString(new File("src/test/examples/transactions-body-1.json"), "UTF-8");
        server.addTransfersRequest(TRANSACTIONS);

        Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(
                LATEST_TRANSACTIONS_REQUEST,
                MediaType.parse("application/json")
            )
        ).build();

        Response response = http.newCall(request).execute();
        then(response.body().string()).isEqualTo(
            String.format(LATEST_TRANSACTION_RESPONSE_FORMAT, TRANSACTIONS)
        );
    }

    @Test
    public void add_error_response() throws Exception {
        final int CODE1 = 401, CODE2 = 500;
        final String MSG1 = "and error", MSG2 = "another error";

        Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                BALANCE_REQUEST_FORMAT, ADDRESS1),
                MediaType.parse("application/json")
            )
        ).build();

        server.addError(CODE1, MSG1);
        Response response = http.newCall(request).execute();

        then(response.code()).isEqualTo(CODE1);
        then(response.body().string()).isEqualTo(MSG1);

        server.addError(CODE2, MSG2);
        response = http.newCall(request).execute();

        then(response.code()).isEqualTo(CODE2);
        then(response.body().string()).isEqualTo(MSG2);
    }

    @Test
    public void reset_clears_mocked_requests() {
        server.addBalanceRequest(WALLET1, new BigDecimal(0));
        then(server.ethereum.getStubMappings()).hasSize(1);
        server.addError(400, "error");
        then(server.ethereum.getStubMappings()).hasSize(2);
        server.reset();
        then(server.ethereum.getStubMappings()).hasSize(0);
    }

    @Test
    public void simultae_connection_failure() {
        server.addFailure();

        Request request = new Request.Builder().url(server.ethereum.url("fake")).post(
            RequestBody.create(String.format(
                BALANCE_REQUEST_FORMAT, ADDRESS1),
                MediaType.parse("application/json")
            )
        ).build();
        try {
            http.newCall(request).execute();
            fail("no connection error detected");
        } catch (IOException x) {
            then(x).isInstanceOf(SocketException.class);
        }
    }

    // --------------------------------------------------------- private methods

    private String givenTransactionsBody(final String set)
    throws IOException {
        return FileUtils.readFileToString(new File("src/test/examples/" + set), "UTF-8");
    }
}
