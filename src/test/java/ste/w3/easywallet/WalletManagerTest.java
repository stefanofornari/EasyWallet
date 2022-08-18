package ste.w3.easywallet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class WalletManagerTest implements TestingConstants {

    public static TestingServer server = null;

    private static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    private static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    @BeforeClass
    public static void before_class() {
        try (InputStream is = WalletTest.class.getClassLoader().
                getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() throws Exception {
        server = new TestingServer();
    }

    @Test
    public void construc_wallet_manager() {
        WalletManager wm = new WalletManager("https://mainnet.infura.io/v3/PROJECTID1", TEST_APP_KEY_1);
        then(wm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID1");
        then(wm.appkey).isEqualTo(TEST_APP_KEY_1);

        wm = new WalletManager("https://mainnet.infura.io/v3/PROJECTID2", TEST_APP_KEY_2);
        then(wm.endpoint).isEqualTo("https://mainnet.infura.io/v3/PROJECTID2");
        then(wm.appkey).isEqualTo(TEST_APP_KEY_2);
    }

    @Test
    public void get_balance() throws Exception {
        WalletManager wm = new WalletManager(server.ethereum.url("v3/PROJECTID1").toString(), TEST_APP_KEY_1);

        try {
            wm.balance(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("wallet can not be null");
        }

        Wallet w = new Wallet(ADDRESS1);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(9343922.000069);

        w = new Wallet(ADDRESS2);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(2113030.001);
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
