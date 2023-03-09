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
import java.util.HashMap;
import java.util.Map;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import com.github.tomakehurst.wiremock.http.Fault;

/**
 *
 */
public class TestingServer implements TestingConstants {

    //
    // NOTE: placeholders in JSON are treated by WireMock accordingly to
    // Jsonunit (https://github.com/lukas-krecan/JsonUnit)
    //

    static public final String TOKEN_BALANCE_REQUEST_FORMAT =
        "{\"jsonrpc\":\"2.0\",\"method\":\"eth_call\",\"params\":[{\"from\":\"0x3f17f1962b36e491b30a40b2405849e597ba5fb5\",\"to\":\"0x%s\",\"data\":\"0x70a08231000000000000000000000000%s\"},\"latest\"],\"id\":\"${json-unit.ignore}\"}";
    static public final String TOKEN_BALANCE_RESPONSE_FORMAT =
        "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x%064x\"}";

    static public final String BALANCE_REQUEST_FORMAT =
        "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x%s\",\"latest\"],\"id\":\"${json-unit.ignore}\"}";
    static public final String BALANCE_RESPONSE_FORMAT =
        "{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x%014x\"}";

    public final WireMockServer ethereum = new WireMockServer(wireMockConfig().dynamicPort());

    public void addBalanceRequest(String address, BigDecimal balance) {
        BigDecimal bigBalance = balance.movePointRight(14);

        String requestBody = String.format(
            BALANCE_REQUEST_FORMAT, address
        );
        String responseBody = String.format(BALANCE_RESPONSE_FORMAT, bigBalance.toBigInteger());

        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(equalToJson(requestBody))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(responseBody.length()))
                            .withBody(responseBody)
                    )
                );
    }

    /*
    {"jsonrpc":"2.0","id":3,"result":"0x0000000000000000000000000000000000000000000000005a850fa96456afd7"}
    symbol: GLM
    name: Golem Network Token (PoS)
    decimal: 18
    balance (0x3eAE5d25Aa262a8821357f8b03545d9a6eB1D9F2)=6.522636855523323863 (6522636855523323863)
     */

    public void addBalanceRequest(Coin coin, String address, BigDecimal balance) {
        BigDecimal bigBalance = balance.movePointRight(coin.decimals);

        String requestBody = String.format(
            TOKEN_BALANCE_REQUEST_FORMAT, coin.contract, address
        );
        String responseBody = String.format(TOKEN_BALANCE_RESPONSE_FORMAT, bigBalance.toBigInteger());

        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(equalToJson(requestBody))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(responseBody.length()))
                            .withBody(responseBody)
                    )
                );
    }

/*
    public void addTransactionsRequest(String address) {
        String body = String.format("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"0x%064x\"}");

        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(containing(address))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(body.length()))
                            .withBody(body)
                    )
                );
    }
*/

    public void addError(int code, String message) {
        ethereum.stubFor(
                    any(anyUrl())
                    .willReturn(
                        aResponse()
                            .withStatus(code)
                            .withHeader("content-type", "plain/text")
                            .withHeader("content-length", String.valueOf(message.length()))
                            .withBody(message)
                    )
                );
    }

    public void addFailure() {
        ethereum.stubFor(
                    any(anyUrl())
                    .willReturn(
                        aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)
                    )
                );

    }

    public void reset() {
        ethereum.resetAll();
    }
}
