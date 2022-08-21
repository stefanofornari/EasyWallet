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

import java.util.function.Function;
import org.web3j.crypto.Bip32ECKeyPair;
import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

/**
 *
 */
public class BIP32Utils {

    public int[] childDerivationPath(final int index) {
        return new int[] {
          44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, index
        };
    }

    public String privateKeyFromMnemonicAndAddress(
        final String mnemonic, final String address, final Function<String, Boolean> callback
    ) {
        final BIP32Utils BIP32 = new BIP32Utils();

        final byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        final int[] path = BIP32.childDerivationPath(0);

        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);

        for (int i=0; i<Integer.MAX_VALUE; ++i) {
            path[path.length-1] = i;
            Bip32ECKeyPair childKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path);
            String key = Numeric.toHexStringNoPrefix(childKeyPair.getPrivateKey());

            if ((callback != null) && (callback.apply(key) == false)) {
                break;
            }

            if (address.equalsIgnoreCase(Keys.getAddress(childKeyPair))) {
                return key;
            }
        }

        return null;  // not found
    }

    public String privateKeyFromMnemonicAndAddress(
        final String mnemonic, final String address
    ) {
        return privateKeyFromMnemonicAndAddress(mnemonic, address, null);
    }
}
