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

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 */
public class EasyWalletExceptionTest {

    @Test
    public void constructor() {
       then(
            new EasyWalletException()
       ).hasMessage("").hasNoCause();

       then(
            new EasyWalletException("a message")
       ).hasMessage("a message").hasNoCause();

       then(
            new EasyWalletException((String)null)
       ).hasMessage(null).hasNoCause();

       Exception e = new Exception("causing message");
       then(
            new EasyWalletException(e)
       ).hasMessage(e.getMessage()).hasCause(e);

       then(
            new EasyWalletException((Throwable)null)
       ).hasMessage("").hasNoCause();

       then(
            new EasyWalletException(e, "outer message")
       ).hasMessage("outer message").hasCause(e);

       then(
            new EasyWalletException(e, "message with format: %s", "parameter")
       ).hasMessage("message with format: parameter").hasCause(e);

    }

}
