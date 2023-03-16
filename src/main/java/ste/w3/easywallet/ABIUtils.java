/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2023 Stefano Fornari. Licensed under the
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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import static ste.w3.easywallet.Utils.unex;

/**
 *
 */
public class ABIUtils {

    public void transactionInputDecode(
        final String input, final Transaction t, final Coin[] coins
    ) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("input can not be null or empty");
        }
        if (input.length() != 138) {
            throw new IllegalArgumentException(
                String.format("input shall be of size 138 (it was %d)", input.length()));
        }
        if (t == null) {
            throw new IllegalArgumentException("transaction can not be null");
        }

        Bytes4 something = TypeDecoder.decode(input.substring(2, 10), Bytes4.class); // I do not know what this is

        Map<String, String> coinMap = new HashMap<>();
        if (coins != null) {
            for (Coin c: coins) {
                if (c.contract != null) {
                    coinMap.put(c.contract.toLowerCase(), c.symbol);
                }
            }
        }

        t.coin = coinMap.getOrDefault(
            unex(TypeDecoder.decode(input.substring(10, 74), Address.class).getValue()),
            "UNKNOWN"
        );
        Uint256 amount = TypeDecoder.decode(input.substring(74), Uint256.class);
        t.amount = Convert.fromWei(amount.getValue().toString(), Unit.ETHER);
    }
}
