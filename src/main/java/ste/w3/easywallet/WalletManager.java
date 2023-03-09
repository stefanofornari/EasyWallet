package ste.w3.easywallet;

import java.io.IOException;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.exceptions.ClientConnectionException;
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

    public WalletManager balance(Wallet wallet, Coin... coins) throws EasyWalletException {
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
                try {
                    EthGetBalance eth = web3.ethGetBalance(
                        "0x" + wallet.address, DefaultBlockParameterName.LATEST
                    ).send();

                    wallet.balance(
                        new Amount(c, Convert.fromWei(eth.getBalance().toString(), Unit.ETHER))
                    );
                } catch (IOException x) {
                    throw new EasyWalletException(x, "Error retrieving balance for %s, check your network", c.symbol);
                } catch (ClientConnectionException x) {
                    throw new EasyWalletException(x, "Error '%s' retrieving balance for %s, check your configuration", x.getMessage(), c.symbol);
                }
            } else {
                ERC20 token = ERC20.load("0x" + c.contract, web3, credentials, new DefaultGasProvider());
                try {
                    wallet.balance(new Amount(
                        c,
                        String.valueOf(token.balanceOf("0x" + wallet.address).send())
                    ));
                } catch (IOException x) {
                    throw new EasyWalletException(x, "Error retrieving balance for %s, check your network", c.symbol);
                } catch (Exception x) {
                    throw new EasyWalletException(x, "Error '%s' retrieving balance for %s, check your configuration", x.getMessage(), c.symbol);
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
