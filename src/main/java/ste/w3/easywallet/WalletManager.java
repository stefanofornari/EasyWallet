package ste.w3.easywallet;

import java.io.IOException;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

/**
 *
 */
public class WalletManager {

    private final Web3j web3;

    public final String endpoint;

    public WalletManager(final String endpoint) {
        this.endpoint = endpoint;
        web3 = Web3j.build(new HttpService(endpoint));
    }

    public WalletManager balance(Wallet wallet, Coin... coins) throws IOException {
        if (wallet == null) {
            throw new IllegalArgumentException("wallet can not be null");
        }

        if (coins == null) return this;

        //
        // Notes:
        // 1) if a coin has null contract, we assume it is the main coin for
        // the network (i.g. ETH for Etherem, MATIC for Polygon)
        // 2) we do not need any credentials to read the balance
        //
        Credentials credentials = Credentials.create("0x0000000000000000000000000000000000000000000000000000000000000000");
        for (Coin c: coins) {
            if (c.contract == null) {
                //
                // NOTE: balance is returned in wei
                //
                EthGetBalance eth = web3.ethGetBalance(
                    "0x" + wallet.address, DefaultBlockParameterName.LATEST
                ).send();

                wallet.balance(
                    new Amount(c, Convert.fromWei(eth.getBalance().toString(), Unit.ETHER))
                );
            } else {
                ERC20 token = ERC20.load(c.contract, web3, credentials, new DefaultGasProvider());
                try {
                    wallet.balance(new Amount(
                        c,
                        String.valueOf(token.balanceOf("0x3eAE5d25Aa262a8821357f8b03545d9a6eB1D9F2").send())
                    ));
                } catch (IOException x) {
                    throw x;
                } catch (Exception x) {
                    //
                    // TODO: handle exception (log it?)
                    //
                    x.printStackTrace();
                }
            }
        }

        return this;
    }

    public static Wallet fromPrivateKey(String key) {
        Wallet w = new Wallet(
            Keys.getAddress(ECKeyPair.create(Numeric.toBigInt(key)))
        );
        w.privateKey = key;

        return w;
    }

}
