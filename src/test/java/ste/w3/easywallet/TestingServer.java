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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import com.github.tomakehurst.wiremock.http.Fault;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.web3j.utils.Numeric;

/**
 *
 */
public class TestingServer implements TestingConstants {

    public static final long A_TIMESTAMP = 1679840558000l;

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
    static public final String LATEST_TRANSACTIONS_REQUEST =
        "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"latest\",true],\"id\":\"${json-unit.ignore}\"}";
    static public final String TRANSACTIONS_REQUEST =
        "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"0x%s\",true],\"id\":\"${json-unit.ignore}\"}";
    static public final String LATEST_TRANSACTION_RESPONSE_FORMAT = "{\n" +
        "\"jsonrpc\": \"2.0\",\n" +
        "\"id\": 0,\n" +
        "\"result\": {\n" +
            "\"baseFeePerGas\": \"0x224cf532e\",\n" +
            "\"difficulty\": \"0x18\",\n" +
            "\"extraData\": \"0xd682021083626f7288676f312e31382e33856c696e7578000000000000000000a0533f454d1aca9a4db4cfd3dd382d77b0454e4c3f22bc282c0dc89b50d3be6b2e78f825009abc42f419a2278951ef43233a2fad023a87a1cb17ad4dbd76014600\",\n" +
            "\"gasLimit\": \"0x1c0d510\",\n" +
            "\"gasUsed\": \"0xce0434\",\n" +
            "\"hash\": \"0xhash%1$060X\",\n" +          // <--- hash
            "\"logsBloom\": \"0x9ca1194892b6c80018c12670a19cb143951f017008a37c44d10324723906294c40061068309b915b86290092c57341410026a1000c266c91213b0014213620103d5c94015b9241ac28745129232e22f9844a1c6e0145140479430cb41c2d22414e8a229923890c90b800ec0905a63e0c09a46d9546184a0da120c010f4c8048348110140660cda8201d8094625aa25d040432d900224d02c4051606446ab0a7062045da355826b0d7724880c55bb208452250eb13b39886c016152b30c7428450305122e22041444660718a41eb9345275e13210908cb6140cbb83080500f242209f65c891c00c0a4f3528d7a0080c0b9098804901e954155c80288a09302a24\",\n" +
            "\"miner\": \"0x0000000000000000000000000000000000000000\",\n" +
            "\"mixHash\": \"0x0000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "\"nonce\": \"0x0000000000000000\",\n" +
            "\"number\": \"0x%1$08X\",\n" +             // <--- number
            "\"parentHash\": \"0x82b4c8559714da01ead67df90db8b4e6d30ce6534531ff14597364fb3e251ecf\",\n" +
            "\"receiptsRoot\": \"0x52495649f13ac872f304d403f637e07b89e866c8aa1c17707ca7bd665c0cd780\",\n" +
            "\"sha3Uncles\": \"0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347\",\n" +
            "\"size\": \"0xa907\",\n" +
            "\"stateRoot\": \"0xc78b6a144993354f9e8eca88893da55bca829bde828dfbdcb2ba458614c9625f\",\n" +
            "\"timestamp\": \"0x%2$08X\",\n" +          // <--- timestamp
            "\"totalDifficulty\": \"0x20f4571e\",\n" +
            "\"transactions\": [%3$s],\n" +             // <--- TRANSACTIONS
            "\"transactionsRoot\": \"0xe252bc5c255ffc81992fbcbfc81fafc456a1bff80890ad2f7fcb8af2df8e9486\",\n" +
            "\"uncles\": []\n" +
        "}\n" +
    "}";

    static public final String TRANSACTION_BODY_FORMAT = "{\n" +
        "\"blockHash\": \"0x941bdb675ea97b27414681303b158b7226d72ad8e6ea17d7345f03c5e2eb9842\",\n" +
        "\"blockNumber\": \"0x21e62ab\",\n" +
        "\"from\": \"0xf6a01c044dedc636f5f93f14bde8a53b4212d0b3\",\n" +
        "\"gas\": \"0x186a0\",\n" +
        "\"gasPrice\": \"0x94133c250\",\n" +
        "\"hash\": \"0x%s\",\n" +                    // <--- HASH
        "\"input\": \"0xa9059cbb%s%s\",\n" +         // <--- INPUT: <method><destination><amount>
        "\"nonce\": \"0xe68a\",\n" +
        "\"r\": \"0x6cd99eba87bb7104ede6e43238b390de6808a38af253b5aad5fc9424017549be\",\n" +
        "\"s\": \"0x55486f168b2776478a8c3a63105b94e74f841f9ae567e501801d35aa13452c13\",\n" +
        "%s" +                                       // <--- contract (as to: is available)
        "\"transactionIndex\": \"0x38\",\n" +
        "\"type\": \"0x0\",\n" +
        "\"v\": \"0x135\",\n" +
        "\"value\": \"0x0\"\n" +
    "}";

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

    public void addLatestTransfersRequest(long block, Transaction[] transactions, Coin[] coins) {
        String responseBody = buildTransactionsResponse(block, transactions, coins); // <- hard-coded block number
        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(equalToJson(LATEST_TRANSACTIONS_REQUEST))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(responseBody.length()))
                            .withBody(responseBody)
                    )
                );
    }

    public void addTransfersRequest(long block, Transaction[] transactions, Coin[] coins) {
        String responseBody = buildTransactionsResponse(block, transactions, coins);
        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(equalToJson(String.format(TRANSACTIONS_REQUEST, BigInteger.valueOf(block).toString(16))))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(responseBody.length()))
                            .withBody(responseBody)
                    )
                );
    }

    public void addTransfersRequest(final String transactions) {
        final String responseBody = String.format(LATEST_TRANSACTION_RESPONSE_FORMAT, 35545771, 1668324371, transactions); // hard-coded block # and timestamp
        ethereum.stubFor(
                    any(anyUrl())
                    .withRequestBody(equalToJson(LATEST_TRANSACTIONS_REQUEST))
                    .willReturn(
                        aResponse()
                            .withHeader("content-type", "application/json")
                            .withHeader("content-length", String.valueOf(responseBody.length()))
                            .withBody(responseBody)
                    )
                );
    }

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

    // -------------------------------------------------------------------------

    private String buildTransactionsBody(Transaction[] transactions, Coin[] coins) {
        Map<String, String> coinMap = new HashMap<>();
        if (coins != null) {
            for (Coin c: coins) {
                coinMap.put(c.symbol, c.contract.toLowerCase());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Transaction t: transactions) {
            if (!sb.isEmpty()) {
                sb.append(',');
            }
            sb.append(String.format(
                TRANSACTION_BODY_FORMAT,
                t.hash(),
                StringUtils.leftPad(t.to, 64, '0'),
                Numeric.toHexStringNoPrefixZeroPadded(t.amount.toBigInteger(), 64),
                (coinMap.get(t.coin) != null) ? ("\"to\": \"0x" + coinMap.get(t.coin) + "\",\n") : ""
            ));
        }

        return sb.toString();

    }

    private String buildTransactionsResponse(long block, Transaction[] transactions, Coin[] coins) {
        return String.format(
            LATEST_TRANSACTION_RESPONSE_FORMAT,
            block,
            ((transactions == null) || (transactions.length == 0))? A_TIMESTAMP/1000 : (long)(transactions[0].when.getTime()/1000),
            buildTransactionsBody(transactions, coins)
        );
    }

}
