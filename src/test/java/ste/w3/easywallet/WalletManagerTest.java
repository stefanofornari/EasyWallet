package ste.w3.easywallet;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class WalletManagerTest {

    public static

    MockWebServer server = null;

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
        server = new MockWebServer();
    }

    @Test
    public void construc_wallet_manager() {
        then(new WalletManager("https://mainnet.infura.io/v3/PROJECTID1").endpoint)
            .isEqualTo("https://mainnet.infura.io/v3/PROJECTID1");
        then(new WalletManager("https://mainnet.infura.io/v3/PROJECTID2").endpoint)
            .isEqualTo("https://mainnet.infura.io/v3/PROJECTID2");
    }

    @Test
    public void get_balance() throws Exception {
        WalletManager wm = new WalletManager(server.url("v3/PROJECTID1").toString());

        Wallet w = new Wallet("0x00000000219ab540356cbb839cbe05303d7705fa");

        try {
            wm.balance(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("wallet can not be null");
        }

        server.enqueue(
           new MockResponse().setHeader("content-type", "application/json")
                             .setBody("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0x7baa706cf4a4220055045\"}"));
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(9343922.000069);

        server.enqueue(
           new MockResponse().setHeader("content-type", "application/json")
                             .setBody(" {\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"0x1bf7395fc44bec91e8000\"}"));
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(2113030.001);
    }

}
