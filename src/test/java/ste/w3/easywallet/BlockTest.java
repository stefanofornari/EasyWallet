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

import java.math.BigInteger;
import java.util.Date;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import wiremock.org.apache.commons.lang3.time.DateUtils;

/**
 *
 */
public class BlockTest {

    @Test
    public void constructor_and_getter() {
        final Date NOW = new Date();
        final Date TOMORROW = DateUtils.addDays(NOW, 1);

        Block b = new Block();
        then(b.hash).isNull();
        then(b.number).isNull();

        b = new Block(NOW, BigInteger.ONE, "hash1");
        then(b.when).isEqualTo(NOW);
        then(b.number).isEqualTo(BigInteger.ONE);
        then(b.hash).isEqualTo("hash1");

        b = new Block(TOMORROW, BigInteger.TEN, "hash2");
        then(b.when).isEqualTo(TOMORROW);
        then(b.number).isEqualTo(BigInteger.TEN);
        then(b.hash).isEqualTo("hash2");

        b = new Block(null, null, null);
        then(b.when).isNull();
        then(b.number).isNull();
        then(b.hash).isNull();
    }

}
