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
import java.time.Instant;
import java.util.Date;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import static ste.w3.easywallet.TestingConstants.GLM;
import static ste.w3.easywallet.TestingConstants.STORJ;

/**
 *
 */
public class TransactionTest {

    private final Date TEST_DATE_1 = new Date(Instant.parse("2022-10-29T22:10:29Z").getEpochSecond()*1000);
    private final Date TEST_DATE_2 = new Date(Instant.parse("2070-08-13T09:09:19Z").getEpochSecond()*1000);

    @Test
    public void constructor_and_getter() {
        Transaction t = new Transaction();
        then(t.coin).isNull();
        then(t.amount).isNull();
        then(t.from).isNull();
        then(t.hash).isNull();
        then(t.when).isNull();

        t = new Transaction(TEST_DATE_1, STORJ, new BigDecimal("11.11"), "from1", "to1", "hash1");
        then(t.coin).isEqualTo(STORJ.symbol); then(t.coin()).isEqualTo(t.coin);
        then(t.amount).isEqualTo("11.11"); then(t.amount()).isEqualTo(t.amount);
        then(t.from).isEqualTo("from1"); then(t.from()).isEqualTo(t.from);
        then(t.hash).isEqualTo("hash1"); then(t.hash()).isEqualTo(t.hash);
        then(t.when).isEqualTo(TEST_DATE_1); then(t.when()).isEqualTo(t.when);

        t = new Transaction(TEST_DATE_2, GLM, new BigDecimal("22.22"), "from2", "to2", "hash2");
        then(t.coin).isEqualTo(GLM.symbol); then(t.coin()).isEqualTo(t.coin);
        then(t.amount).isEqualTo("22.22"); then(t.amount()).isEqualTo(t.amount);
        then(t.from).isEqualTo("from2"); then(t.from()).isEqualTo(t.from);
        then(t.hash).isEqualTo("hash2"); then(t.hash()).isEqualTo(t.hash);
        then(t.when).isEqualTo(TEST_DATE_2); then(t.when()).isEqualTo(t.when);
    }

    @Test
    public void get_when_in_zulu() {
        then(new Transaction(TEST_DATE_1, STORJ, new BigDecimal("11.11"), "from1", "to1", "hash1").whenZ()).isEqualTo(TEST_DATE_1.toString());
        then(new Transaction(TEST_DATE_2, GLM, new BigDecimal("11.11"), "from1", "to1", "hash1").whenZ()).isEqualTo(TEST_DATE_2.toString());
    }

}
