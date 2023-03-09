package ste.w3.easywallet;

import java.math.BigDecimal;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
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
        server.ethereum.start();
    }

    @After
    public void after() throws Exception {
        server.ethereum.stop();
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
        server.addBalanceRequest(ADDRESS1, new BigDecimal("9343922.000069"));
        server.addBalanceRequest(STORJ, ADDRESS1, new BigDecimal("534.09876543"));
        server.addBalanceRequest(ADDRESS2, new BigDecimal("2113030.001"));
        server.addBalanceRequest(STORJ, ADDRESS2, new BigDecimal("123.456789"));

        WalletManager wm = new WalletManager(server.ethereum.url("v3/PROJECTID1/" + TEST_APP_KEY_1));

        try {
            wm.balance(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("wallet can not be null");
        }

        Wallet w = new Wallet(ADDRESS1);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balances).isEmpty();

        wm.balance(w, ETH, STORJ);

        then(w.balance(ETH)).isEqualTo("934.3922000069");
        then(w.balance(STORJ)).isEqualTo("534.09876543");

        w = new Wallet(ADDRESS2);
        then(wm.balance(w, ETH, STORJ)).isSameAs(wm);
        then(w.balance(ETH)).isEqualTo("211.3030001");
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

    @Test
    public void reports_client_errors() throws Exception {
        // Invalid response received: 401; project id required in the url
        // Invalid response received: 401; invalid project id
        // IOException
        /*
        HTTP/1.1 401 Unauthorized
        Date: Thu, 10 Nov 2022 07:42:04 GMT
        Content-Type: text/plain; charset=utf-8
        Content-Length: 31
        Connection: keep-alive
        Vary: Accept-Encoding
        Vary: Origin
        Www-Authenticate: Basic realm="Project ID is required in the URL"
        X-Content-Type-Options: nosniff

        project id required in the url
        */

        server.addError(401, "project id required in the url");


        WalletManager wm = new WalletManager(server.ethereum.url("v3"));
        try {
            wm.balance(new Wallet(ADDRESS1), ETH);
        } catch (EasyWalletException x) {
            then(x).hasMessageContaining("project id required in the url");
        }

        server.reset();
        server.addError(401, "invalid project id");

        try {
            wm.balance(new Wallet(ADDRESS1), ETH);
        } catch (EasyWalletException x) {
            then(x).hasMessageContaining("invalid project id");
        }
    }

    @Test
    public void reports_connection_errors() throws Exception {
        server.addFailure();

        WalletManager wm = new WalletManager(server.ethereum.url("v3"));
        try {
            wm.balance(new Wallet(ADDRESS1), ETH);
        } catch (EasyWalletException x) {
            then(x).hasMessageContaining("check your network");
        }
    }
}
