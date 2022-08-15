package ste.w3.easywallet;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class WalletManagerTest implements TestingConstants {

    public static MockWebServer server = null;

    private static final String TEST_APP_KEY_1 = "THSISANAPPKEY";
    private static final String TEST_APP_KEY_2 = "THISISANOTHERAPPKEY";

    private static final Map<String, String> TEST_BALANCE = new HashMap<>();

    @BeforeClass
    public static void before_class() {
        try (InputStream is = WalletTest.class.getClassLoader().
                getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TEST_BALANCE.put(WALLET1, "0x7baa706cf4a4220055045");
        TEST_BALANCE.put(WALLET2, "0x1bf7395fc44bec91e8000");
    }

    @Before
    public void before() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(dispatcher());
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
        WalletManager wm = new WalletManager(server.url("v3/PROJECTID1").toString(), TEST_APP_KEY_1);

        try {
            wm.balance(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("wallet can not be null");
        }

        Wallet w = new Wallet(WALLET1);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(9343922.000069);

        w = new Wallet(WALLET2);
        then(wm.balance(w)).isSameAs(wm);
        then(w.balance().doubleValue()).isEqualTo(2113030.001);
    }

    // --------------------------------------------------------- private methods

    private Dispatcher dispatcher() {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest r) throws InterruptedException {
                Gson g = new Gson();

                HashMap body = g.fromJson(r.getBody().readUtf8(), HashMap.class);
                String address = (String)((List)body.get("params")).get(0);

                return new MockResponse().setHeader("content-type", "application/json")
                    .setBody(String.format(
                        "{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":\"%s\"}", TEST_BALANCE.get(address)
                    )
                );
            }

        };
    }

}
