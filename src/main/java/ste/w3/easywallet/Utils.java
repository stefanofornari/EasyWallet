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

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO: rename to Utils (or delete it)
 */
public class Utils {

    public static void printWallets(Wallet[] wallets) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(wallets));
    }

    public static String ex(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("s can not be null");
        }

        return (s.startsWith("0x")) ? s : ("0x" + s);
    }

    public static String unex(final String s) {
        if (s == null) {
            return null;
        }

        return (s.startsWith("0x")) ? s.substring(2) : s;
    }

}
