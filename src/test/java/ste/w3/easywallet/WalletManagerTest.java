package ste.w3.easywallet;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import okhttp3.mockwebserver.RecordedRequest;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class WalletManagerTest implements TestingConstants {

    public static TestingServer server = null;

    private static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    private static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    @Before
    public void before() throws Exception {
        server = new TestingServer();
    }

    @Test
    public void construct_wallet_manager() {
        WalletManager wm = new WalletManager("https://mainnet.infura.io/v3/PROJECTID1/"+ TEST_APP_KEY_1);
        then(wm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID1/" + TEST_APP_KEY_1);

        wm = new WalletManager("https://mainnet.infura.io/v3/PROJECTID2/" + TEST_APP_KEY_2);
        then(wm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID2/" + TEST_APP_KEY_2);
    }

    @Test
    public void get_balance() throws Exception {
        server.addBalanceRequest(ETH, "0x" + ADDRESS1, new BigDecimal("9343922.000069"));
        server.addBalanceRequest(STORJ, "0x" + ADDRESS1, new BigDecimal("534.09876543"));
        server.addBalanceRequest(ETH, "0x" + ADDRESS2, new BigDecimal("2113030.001"));
        server.addBalanceRequest(STORJ, "0x" + ADDRESS2, new BigDecimal("123.456789"));

        WalletManager wm = new WalletManager(server.ethereum.url("v3/PROJECTID1/" + TEST_APP_KEY_1).toString());

        try {
            wm.balance(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("wallet can not be null");
        }

        Wallet w = new Wallet(ADDRESS1);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balances).isEmpty();
        then(server.ethereum.getRequestCount()).isZero();

        wm.balance(w, ETH, STORJ);
        then(server.ethereum.getRequestCount()).isEqualTo(2); // ETH and STORJ
        RecordedRequest r = server.ethereum.takeRequest();
        then(r.getBody().readString(Charset.defaultCharset())).contains("\"method\":\"eth_getBalance\"");
        r = server.ethereum.takeRequest();
        then(r.getBody().readString(Charset.defaultCharset())).contains("\"method\":\"eth_call\"");

        then(w.balance(ETH)).isEqualTo("9343922.000069");
        then(w.balance(STORJ)).isEqualTo("534.09876543");

        w = new Wallet(ADDRESS2);
        then(wm.balance(w, ETH, STORJ)).isSameAs(wm);
        then(w.balance(ETH)).isEqualTo("2113030.001");
        then(w.balance(STORJ)).isEqualTo("123.45678900");
    }

    @Test
    public void wallet_from_private_key() {
        then(WalletManager.fromPrivateKey(PRIVATE_KEY1).address).isEqualTo(ADDRESS1);
        then(WalletManager.fromPrivateKey(PRIVATE_KEY2).address).isEqualTo(ADDRESS2);
        try {
            then(WalletManager.fromPrivateKey('x' + PRIVATE_KEY2.substring(1)).address).isEqualTo(ADDRESS2);
            fail("invalid key not checked");
        } catch (NumberFormatException x) {
            // OK
        }
    }
}
