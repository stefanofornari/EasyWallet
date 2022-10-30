
package ste.w3.easywallet;

import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.Coin.ETH;

/**
 *
 */
public class WalletTest implements TestingConstants {

    @Test
    public void construct_wallet() {
        Wallet w = new Wallet("0x000102030405060708090A0B0C0D0E0F10111213");
        then(w.address).isEqualTo("0x000102030405060708090A0B0C0D0E0F10111213");

        w = new Wallet("0x00000000219ab540356cBB839Cbe05303d7705Fa");
        then(w.address).isEqualTo("0x00000000219ab540356cBB839Cbe05303d7705Fa");
    }

    @Test
    public void get_and_set_wallet_balance() {
        Wallet w = new Wallet("0x00000000219ab540356cBB839Cbe05303d7705Fa");

        try {
            w.balance((Coin)null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("coin can not be null");
        }

        try {
            w.balance((Amount)null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("balance can not be null");
        }
        then(w.balance(new Amount(ETH, "10000000000000000000"))).isSameAs(w);
        then(w.balance(ETH).doubleValue()).isEqualTo(10.0);
    }

    //
    // TODO: what to do if asking the balance of a unset coin
    //
}
