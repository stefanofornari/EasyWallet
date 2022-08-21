/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2022 Stefano Fornari. Licensed under the
 * EUPL-1.2 or later (see LICENSE).
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

/**
 *
 */
public class MnemonicTest implements TestingConstants {

    public final BIP32Utils bip32 = new BIP32Utils();

    @Test
    public void mnemonic_to_extended_key() {
        then(
            MnemonicUtils.generateSeed(MNEMONIC1, null)
        ).isEqualTo(Numeric.hexStringToByteArray(SEED1));

        then(
            MnemonicUtils.generateSeed(MNEMONIC2, null)
        ).isEqualTo(Numeric.hexStringToByteArray(SEED2));
    }

    @Test
    public void mnemonic_to_private_ket() {
        byte[] seed = MnemonicUtils.generateSeed(MNEMONIC3, null);

        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);

        then(
            Numeric.toHexStringNoPrefix(
                Bip32ECKeyPair.deriveKeyPair(masterKeyPair, bip32.childDerivationPath(0)).getPrivateKey()
            )
        ).isEqualTo(PRIVATE_KEY6);

        then(
            Numeric.toHexStringNoPrefix(
                Bip32ECKeyPair.deriveKeyPair(masterKeyPair, bip32.childDerivationPath(3824)).getPrivateKey()
            )
        ).isEqualTo(PRIVATE_KEY7);
    }

    @Test(timeout = 3000)
    public void private_key_from_mnemonic_and_address() {
        then(
            bip32.privateKeyFromMnemonicAndAddress(MNEMONIC3, ADDRESS6)
        ).isEqualTo(PRIVATE_KEY6);

        then(
            bip32.privateKeyFromMnemonicAndAddress(MNEMONIC3, ADDRESS3)
        ).isEqualTo(PRIVATE_KEY3);

        then(
            bip32.privateKeyFromMnemonicAndAddress(MNEMONIC3, ADDRESS6.toUpperCase())
        ).isEqualTo(PRIVATE_KEY6);
    }

    @Test(timeout = 3000)
    public void private_key_from_mnemonic_and_address_with_callback() {
        final List<String> a = new ArrayList<>();

        Function<String, Boolean> callback = new Function<>() {
             public Boolean apply(String key) {
                a.add(key); return true;
            }
        };

        then(
            bip32.privateKeyFromMnemonicAndAddress(MNEMONIC3, ADDRESS8, callback)
        ).isEqualTo(PRIVATE_KEY8);

        then(a).containsExactly(
            "82b4cd6699cc1aee53b492598def7833a5ca8aae948f817c325548cb3e62c610",
            "32fb8c9a979a82eaadaf40c288202b2b19d955d8293241e89c19b1cfb5a5b503",
            "21de2c51db9ac0cf564140c6e803036ffc22c0879a21fdf57866aca969aacdbc"
        );

    }

    @Test(timeout = 3000)
    public void private_key_from_mnemonic_and_address_with_callback_stop() {
        final List<String> a = new ArrayList<>();

        Function<String, Boolean> callback = new Function<>() {
            int i = 0;

            public Boolean apply(String key) {
                a.add(key); return (i++<1);
            }
        };

        then(
            bip32.privateKeyFromMnemonicAndAddress(MNEMONIC3, ADDRESS8, callback)
        ).isNull();

        then(a).containsExactly(
            "82b4cd6699cc1aee53b492598def7833a5ca8aae948f817c325548cb3e62c610",
            "32fb8c9a979a82eaadaf40c288202b2b19d955d8293241e89c19b1cfb5a5b503"
        );

    }

}
