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

    public static final byte[] INCOMING_COIN = { (byte)0xa9, (byte)0x05, (byte)0x9c, (byte)0xbb };

    public void tranferInputDecode(String input, final Transaction t) {
        input = unex(input);

        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("input can not be null or empty");
        }
        if (input.length() != 136) {
            throw new IllegalArgumentException(
                String.format("input without 0x shall be of size 136 (it was %d)", input.length()));
        }
        if (t == null) {
            throw new IllegalArgumentException("transaction can not be null");
        }

        byte[] method = TypeDecoder.decode(input.substring(0, 8), Bytes4.class).getValue();
        for (int i=0; i<INCOMING_COIN.length; ++i) {
            if (method[i] != INCOMING_COIN[i]) {
                throw new IllegalArgumentException(
                    String.format(
                        "not incoming coin transaction (0x%02x%02x%02x%02x)",
                        method[0], method[1], method[2], method[3]
                    )
                );
            }
        }

        t.to = unex(TypeDecoder.decode(input.substring(8, 72), Address.class).getValue());
        Uint256 amount = TypeDecoder.decode(input.substring(72), Uint256.class);
        t.amount = Convert.fromWei(amount.getValue().toString(), Unit.ETHER);
    }
}
