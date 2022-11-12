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
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import static ste.w3.easywallet.ui.Constants.CONFIG_FILE;


/**
 * NOTE: this is not a unit test, it is meant to try the functionality of the
 * api. It uses the default configuration in <code>Constants.CONFIG_FILE</code>
 */
@Ignore
public class ERC20Test {

    private Preferences preferences;

    @Before
    public void before() throws Exception {
        PreferencesManager pm = new PreferencesManager();

        preferences = pm.fromJSON(
            FileUtils.readFileToString(
                new File(FileUtils.getUserDirectory(), CONFIG_FILE),
                "utf8"
            )
        );
    }

    /*
    token.symbol().send()
    =====================

    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 188
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","method":"eth_call","params":[{"from":"0x3f17f1962b36e491b30a40b2405849e597ba5fb5","to":"0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792","data":"0x95d89b41"},"latest"],"id":0}
    --> END POST (188-byte body)
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (1033ms)
    date: Sun, 23 Oct 2022 09:14:31 GMT
    content-type: application/json
    content-length: 230
    vary: Accept-Encoding
    vary: Origin
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","id":0,"result":"0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000553544f524a000000000000000000000000000000000000000000000000000000"}
    <-- END HTTP (230-byte body)

    token.name().send()
    ===================

    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 188
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","method":"eth_call","params":[{"from":"0x3f17f1962b36e491b30a40b2405849e597ba5fb5","to":"0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792","data":"0x06fdde03"},"latest"],"id":1}
    --> END POST (188-byte body)
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (127ms)
    date: Sun, 23 Oct 2022 09:14:31 GMT
    content-type: application/json
    content-length: 230
    vary: Accept-Encoding
    vary: Origin
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","id":1,"result":"0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001053746f726a546f6b656e2028506f532900000000000000000000000000000000"}
    <-- END HTTP (230-byte body)

    token.decimals().send()
    =======================

    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 188
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","method":"eth_call","params":[{"from":"0x3f17f1962b36e491b30a40b2405849e597ba5fb5","to":"0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792","data":"0x313ce567"},"latest"],"id":2}
    --> END POST (188-byte body)
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (132ms)
    date: Sun, 23 Oct 2022 09:14:31 GMT
    content-type: application/json
    content-length: 102
    vary: Accept-Encoding
    vary: Origin
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","id":2,"result":"0x0000000000000000000000000000000000000000000000000000000000000008"}
    <-- END HTTP (102-byte body)

    token.balanceOf
    ===============

    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 252
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","method":"eth_call","params":[{"from":"0x3f17f1962b36e491b30a40b2405849e597ba5fb5","to":"0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792","data":"0x70a082310000000000000000000000003eae5d25aa262a8821357f8b03545d9a6eb1d9f2"},"latest"],"id":3}
    --> END POST (252-byte body)
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (121ms)
    date: Sun, 23 Oct 2022 09:14:31 GMT
    content-type: application/json
    content-length: 102
    vary: Accept-Encoding
    vary: Origin
    [main] DEBUG org.web3j.protocol.http.HttpService -
    {"jsonrpc":"2.0","id":3,"result":"0x000000000000000000000000000000000000000000000000000000011a2f36c1"}
    <-- END HTTP (102-byte body)

    */


    @Test
    public void main() throws Exception {
        final Logger LOG = LoggerFactory.getLogger("ste.easywallet");
        final String WALLET = "0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf";

        TestingServer server = new TestingServer();

        Web3j web3j = Web3j.build(new HttpService(preferences.url()));

        //
        // Create and use fake credentuials (no credentials are needed to get
        // tokens balance
        //
        String pk = "0x0000000000000000000000000000000000000000000000000000000000000000";
        Credentials credentials = Credentials.create(pk);

        //
        // Load the contract
        //
        String contractAddress1 = "0xb64ef51c888972c908cfacf59b47c1afbc0ab8ac";  // STORJ Ethereum
        String contractAddress2 = "0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792";  // STORJ Polygon
        String contractAddress3 = "0x0B220b82F3eA3B7F6d9A1D8ab58930C064A2b5Bf";  // GLM polygon
        ERC20 token = ERC20.load(contractAddress1, web3j, credentials, new DefaultGasProvider());

        LOG.debug("SYMBOL");
        String symbol = token.symbol().send();
        LOG.debug("NAME");
        String name = token.name().send();
        LOG.debug("DECIMALS");
        BigInteger decimal = token.decimals().send();

        LOG.debug("BALANCE");
        BigInteger balance1 = token.balanceOf(WALLET).send();

        System.out.println("symbol: " + symbol);
        System.out.println("name: " + name);
        System.out.println("decimal: " + decimal.intValueExact());
        System.out.println("balance (" + WALLET + ")=" +
           new BigDecimal(balance1).divide(BigDecimal.TEN.pow(decimal.intValue())) + " (" + balance1 + ")");
    }

}
