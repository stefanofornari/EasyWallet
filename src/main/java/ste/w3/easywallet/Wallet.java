package ste.w3.easywallet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Wallet {

    public final String address;

    public String privateKey = "";
    public String mnemonicPhrase = "";

    final private Map<String, BigDecimal> balances = new HashMap<>();

    public Wallet(String address) {
        this.address = address;
    }

    public Wallet balance(Amount balance) {
        if (balance == null) {
            throw new IllegalArgumentException("balance can not be null");
        }
        balances.put(balance.symbol, balance.value);

        return this;
    }

    public BigDecimal balance(Coin coin)  {
        if (coin == null) {
            throw new IllegalArgumentException("coin can not be null");
        }
        return balances.get(coin.symbol);
    }

}
