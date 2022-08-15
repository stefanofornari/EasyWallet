package ste.w3.easywallet;

import java.io.IOException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

/**
 *
 */
public class WalletManager {

    private final Web3j web3;

    public final String endpoint;
    public final String appkey;

    public WalletManager(final String endpoint, final String appkey) {
        this.endpoint = endpoint;
        this.appkey = appkey;
        web3 = Web3j.build(new HttpService(endpoint));
    }

    public WalletManager balance(Wallet wallet) throws IOException {
        if (wallet == null) {
            throw new IllegalArgumentException("wallet can not be null");
        }

        //
        // NOTE: balance is returned in wei
        //
        EthGetBalance balance = web3.ethGetBalance(
            "0x" + wallet.address, DefaultBlockParameterName.LATEST
        ).send();

        wallet.balance(
            Convert.fromWei(balance.getBalance().toString(), Unit.ETHER)
        );

        return this;
    }

}
