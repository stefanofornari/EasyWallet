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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
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
    token.symbol
    ============

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

    token.name
    ==========

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

    token.decimals
    ==============

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
    {"jsonrpc":"2.0","method":"eth_call","params":[{"from":"0x3f17f1962b36e491b30a40b2405849e597ba5fb5","to":"0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792","data":"0x70a08231000000000000000000000000da9dfa130df4de4673b89022ee50ff26f6ea73cf"},"latest"],"id":3}
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
    public void get_token_balance() throws Exception {
        final String WALLET = "0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf";

        Web3j w3 = Web3j.build(new HttpService(preferences.url()));

        //
        // Create and use fake credentials (no credentials are needed to get
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
        ERC20 token = ERC20.load(contractAddress1, w3, credentials, new DefaultGasProvider());

        System.out.append("SYMBOL");
        String symbol = token.symbol().send();
        System.out.append("NAME");
        String name = token.name().send();
        System.out.append("DECIMAL");
        BigInteger decimal = token.decimals().send();
        System.out.append("BALANCE");
        BigInteger balance1 = token.balanceOf(WALLET).send();

        System.out.println("symbol: " + symbol);
        System.out.println("name: " + name);
        System.out.println("decimal: " + decimal.intValueExact());
        System.out.println("balance (" + WALLET + ")="
                + new BigDecimal(balance1).divide(BigDecimal.TEN.pow(decimal.intValue())) + " (" + balance1 + ")");
    }

    /*

    getBalance
    ==========

    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 115

    {"jsonrpc":"2.0","method":"eth_getBalance","params":["0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf","latest"],"id":0}
    --> END POST (115-byte body)
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (988ms)
    date: Mon, 06 Mar 2023 07:13:41 GMT
    content-type: application/json
    content-length: 51
    vary: Origin
    vary: Accept-Encoding

    {"jsonrpc":"2.0","id":0,"result":"0x3f18c57bf4400"}
    <-- END HTTP (51-byte body)

    balance: 0.00111001

     */
    @Test
    public void get_balance() throws Exception {
        final String WALLET = "0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf";

        Web3j w3 = Web3j.build(new HttpService(preferences.url()));

        //
        // Create and use fake credentials (no credentials are needed to get
        // tokens balance
        //
        String pk = "0x0000000000000000000000000000000000000000000000000000000000000000";
        Credentials credentials = Credentials.create(pk);

        EthGetBalance eth = w3.ethGetBalance(
                WALLET, DefaultBlockParameterName.LATEST
        ).send();

        System.out.println("balance: " + Convert.fromWei(eth.getBalance().toString(), Unit.ETHER));
    }

    /*
    getBlockByNumber
    ================
    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 84

    {"jsonrpc":"2.0","method":"eth_getBlockByNumber","params":["0x21e62ab",true],"id":0}
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY
    date: Thu, 09 Mar 2023 21:05:43 GMT
    content-type: application/json
    content-length: 45
    vary: Origin
    vary: Accept-Encoding

    {"jsonrpc":"2.0","id":0,"result":"0x264c70f"}

    getBlockByNumber (latest)
    =========================
    --> POST https://polygon-mainnet.infura.io/v3/$APPKEY
    Content-Type: application/json; charset=utf-8
    Content-Length: 82

    {"jsonrpc":"2.0","method":"eth_getBlockByNumber","params":["latest",false],"id":1}
    <-- 200 https://polygon-mainnet.infura.io/v3/$APPKEY (4297ms)
    date: Sun, 05 Feb 2023 17:06:19 GMT
    content-type: application/json
    vary: Origin
    vary: Accept-Encoding

    [see src/test/examples/block-35545771.json]
     */
    @Test
    public void get_transactions() throws Exception {
        /*
        Nov-13-2022 07:26:11
         */
        Web3j w3 = Web3j.build(new HttpService(preferences.url()));
        //BigInteger blockNumber = w3.ethBlockNumber().send().getBlockNumber();
        BigInteger blockNumber = BigInteger.valueOf(35545771);

        int i = 1, n = 10;
        do {
            //Block block = w3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock();
            Block block = w3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
            System.out.println("Block " + block.getNumber() + " - " + new Date(block.getTimestamp().longValueExact() * 1000) + " " + block.getAuthor());
            List<EthBlock.TransactionResult> txs = block.getTransactions();

            txs.forEach(tx -> {
                EthBlock.TransactionObject t = (EthBlock.TransactionObject) tx.get();
                System.out.println(t.getHash());

                if ("0x0b220b82f3ea3b7f6d9a1d8ab58930c064a2b5bf".equalsIgnoreCase(t.getTo())) {
                    System.out.println(">>>" + t.getFrom() + " " + t.getTo() + " " + t.getValueRaw());
                    Function f = new Function(
                            "0x40c10f19",
                            Collections.<Type>emptyList(),
                            Arrays.asList(
                                    //                            new TypeReference<Uint>() {},
                                    new TypeReference<Address>() {
                            },
                                    new TypeReference<Uint256>() {
                            }
                            )
                    );
                    List<Type> data = FunctionReturnDecoder.decode(t.getInput(), f.getOutputParameters());
                    System.out.println(">>>" + data);

                }
            });

            blockNumber.subtract(BigInteger.ONE);
        } while (++i <= n);

    }

    @Test
    public void input_decoding() throws Exception {
        final String INPUT = "0xa9059cbb000000000000000000000000652328ab1a9746e59d0995e8b5ae1de3f512bd6e0000000000000000000000000000000000000000000000000102a7121cb5ce2a";

        System.out.println("method: " + INPUT.substring(0, 10));
        System.out.println("to: " + INPUT.substring(10, 74));
        System.out.println("amount: " + INPUT.substring(74));


        System.out.println(TypeDecoder.decode(INPUT.substring(2, 10), Bytes4.class));
        System.out.println(TypeDecoder.decode(INPUT.substring(10, 74), Address.class));
        Uint256 amount = TypeDecoder.decode(INPUT.substring(74), Uint256.class);
        System.out.println(Convert.fromWei(amount.getValue().toString(), Unit.ETHER));

        /*
        Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
        refMethod.setAccessible(true);
        Address address = (Address) refMethod.invoke(null, INPUT.substring(10, 74), 0, Address.class);
        System.out.println(address.toString());
        Uint256 amount = (Uint256) refMethod.invoke(null, INPUT.substring(74), 0, Uint256.class);
        System.out.println(amount.getValue());
        */
    }

}
