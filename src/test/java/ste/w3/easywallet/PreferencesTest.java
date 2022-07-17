/*
 * Copyright (C) 2022 Stefano Fornari.
 * Licensed under the EUPL-1.2 or later.
 *
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Stefano Fornari.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * STEFANO FORNARI MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. STEFANO FORNARI SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package ste.w3.easywallet;

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 */
public class PreferencesTest {

    @Test
    public void serialize_empty_preferences() {
        then(new PreferencesManager().toJSON(new Preferences())).isEqualTo("{\"endpoint\":\"\",\"appkey\":\"\",\"wallets\":[]}");
    }

    @Test
    public void serialize_with_endpoint_and_appkey() {
        PreferencesManager pm = new PreferencesManager();
        Preferences p = new Preferences();

        p.endpoint = "this is the endpoint";
        then(pm.toJSON(p)).isEqualTo("{\"endpoint\":\"this is the endpoint\",\"appkey\":\"\",\"wallets\":[]}");

        p.appkey = "this is the appkey";
        then(pm.toJSON(p)).isEqualTo("{\"endpoint\":\"this is the endpoint\",\"appkey\":\"this is the appkey\",\"wallets\":[]}");
    }

    @Test
    public void serialize_with_wallets() {
        PreferencesManager pm = new PreferencesManager();
        Preferences p = new Preferences();

        p.endpoint = "endpoint";
        p.appkey = "appkey";

        p.wallets = new Wallet[] {
          new Wallet("wallet1")
        };
        p.wallets[0].privateKey = "privatekey1";
        then(pm.toJSON(p)).isEqualTo("{\"endpoint\":\"endpoint\",\"appkey\":\"appkey\",\"wallets\":[{\"address\":\"wallet1\",\"privateKey\":\"privatekey1\",\"mnemonicPhrase\":\"\"}]}");

        p.wallets = new Wallet[] { p.wallets[0], new Wallet("wallet2") };
        p.wallets[1].privateKey = "privatekey2";
        p.wallets[1].mnemonicPhrase = "mnemonic2";
        then(pm.toJSON(p)).isEqualTo("{\"endpoint\":\"endpoint\",\"appkey\":\"appkey\",\"wallets\":[{\"address\":\"wallet1\",\"privateKey\":\"privatekey1\",\"mnemonicPhrase\":\"\"},{\"address\":\"wallet2\",\"privateKey\":\"privatekey2\",\"mnemonicPhrase\":\"mnemonic2\"}]}");
    }

    @Test
    public void deserialize_preferences() {
        PreferencesManager pm = new PreferencesManager();

        //
        // empty values
        //

        Preferences p = pm.fromJSON("{\"endpoint\":\"\",\"appkey\":\"\",\"wallets\":[]}");
        then(p.endpoint).isEmpty();
        then(p.appkey).isEmpty();
        then(p.wallets).isEmpty();

        //
        // some values
        //
        p = pm.fromJSON("{\"endpoint\":\"an endpoint\",\"appkey\":\"an appkey\",\"wallets\":[{\"address\":\"a wallet\",\"privateKey\":\"privatekey\",\"mnemonicPhrase\":\"mnemonicphrase\"}]}");
        then(p.endpoint).isEqualTo("an endpoint");
        then(p.appkey).isEqualTo("an appkey");
        then(p.wallets).hasSize(1);
        then(p.wallets[0].address).isEqualTo("a wallet");
        then(p.wallets[0].privateKey).isEqualTo("privatekey");
        then(p.wallets[0].mnemonicPhrase).isEqualTo("mnemonicphrase");

        //
        // empty private key, missing mnemonic
        //
        p = pm.fromJSON("{\"endpoint\":\"an endpoint\",\"appkey\":\"an appkey\",\"wallets\":[{\"address\":\"a wallet\",\"privateKey\":\"\"}]}");
        then(p.endpoint).isEqualTo("an endpoint");
        then(p.appkey).isEqualTo("an appkey");
        then(p.wallets[0].address).isEqualTo("a wallet");
        then(p.wallets[0].privateKey).isEmpty();
        then(p.wallets[0].mnemonicPhrase).isNull();

        //
        // missing private key, empty mnemonic
        //
        p = pm.fromJSON("{\"endpoint\":\"an endpoint\",\"appkey\":\"an appkey\",\"wallets\":[{\"address\":\"a wallet\",\"mnemonic\":\"\"}]}");
        then(p.endpoint).isEqualTo("an endpoint");
        then(p.appkey).isEqualTo("an appkey");
        then(p.wallets[0].address).isEqualTo("a wallet");
        then(p.wallets[0].privateKey).isNull();
        then(p.wallets[0].mnemonicPhrase).isNull();

    }

    @Test
    public void deserialize_preferences_with_missing_values() {
        PreferencesManager pm = new PreferencesManager();

        //
        // no wallets
        //
        Preferences p = pm.fromJSON("{\"endpoint\":\"\",\"appkey\":\"\"}");
        then(p.endpoint).isEmpty();
        then(p.appkey).isEmpty();
        then(p.wallets).isEmpty();

        //
        // no endpoint
        //
        p = pm.fromJSON("{\"appkey\":\"\",\"wallets\":[{\"address\":\"a wallet\"}]}");
        then(p.endpoint).isEmpty();
        then(p.appkey).isEmpty();
        then(p.wallets).isNotEmpty();

        //
        // no appkey
        //
        p = pm.fromJSON("{\"endpoint\":\"endpoint\",\"wallets\":[{\"address\":\"a wallet\"}]}");
        then(p.endpoint).isEqualTo("endpoint");
        then(p.appkey).isEmpty();
        then(p.wallets).isNotEmpty();

        //
        // no values
        //
        p = pm.fromJSON("{}");
        then(p.endpoint).isEmpty();
        then(p.appkey).isEmpty();
        then(p.wallets).isEmpty();
  }


}
