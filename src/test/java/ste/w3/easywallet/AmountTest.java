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

import java.math.BigDecimal;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.ETH;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;

/**
 *
 */
public class AmountTest {

    @Test
    public void constructors() {
        then(new Amount(STORJ, "934480744378454").value).isEqualTo(new BigDecimal("9344807.44378454"));
        then(new Amount(STORJ, "123456789000000").value).isEqualTo(new BigDecimal("1234567.89000000"));

        then(new Amount(GLM, "934480744378454").value).isEqualTo(new BigDecimal("0.000934480744378454"));
        then(new Amount(GLM, "123456789000000").value).isEqualTo(new BigDecimal("0.000123456789000000"));

        then(new Amount(ETH, new BigDecimal("10.0")).value).isEqualTo(new BigDecimal("10.0"));
    }

    @Test
    public void asDouble() {
        then(new Amount(STORJ, "934480744378454").asDouble()).isEqualTo(9344807.44378454);
        then(new Amount(STORJ, "123456789000000").asDouble()).isEqualTo(1234567.89000000);
    }

    @Test
    public void asString() {
        then(new Amount(STORJ, "934480744378454").toString()).isEqualTo("9344807.44378454");
        then(new Amount(GLM, "123456789000000").toString()).isEqualTo("0.000123456789000000");
    }

}
