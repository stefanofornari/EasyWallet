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

import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 */
public class UtilsTest {

    @Test
    public void ex_adds_0x_if_not_present() {
        try {
            Utils.ex(null);
            fail("missing sanity check");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("s can not be null");
        }

        then(Utils.ex("")).isEqualTo("0x");
        then(Utils.ex("0x")).isEqualTo("0x");
        then(Utils.ex("123abc")).isEqualTo("0x123abc");
        then(Utils.ex("0x123abc")).isEqualTo("0x123abc");
        then(Utils.ex("0X123abc")).isEqualTo("0x0X123abc");
    }

    @Test
    public void unex_removes_0x_if_present() {
        then(Utils.unex(null)).isNull();
        then(Utils.unex("")).isEqualTo("");
        then(Utils.unex("0x")).isEqualTo("");
        then(Utils.unex("123abc")).isEqualTo("123abc");
        then(Utils.unex("0x123abc")).isEqualTo("123abc");
        then(Utils.unex("0X123abc")).isEqualTo("0X123abc");
    }

}
