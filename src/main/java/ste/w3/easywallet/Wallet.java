package ste.w3.easywallet;

import java.math.BigDecimal;

/**
 *
 */
public class Wallet {

    public final String address;

    public String privateKey = "";
    public String mnemonicPhrase = "";

    transient private BigDecimal balance;

    public Wallet(String address) {
        this.address = address;
        balance = BigDecimal.ZERO;
    }

    public Wallet balance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("balance can not be null");
        }
        this.balance = balance;
        return this;
    }

    public BigDecimal balance()  {
        return balance;
    }

}
