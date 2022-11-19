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

import java.time.Instant;

/**
 * TODO: use better types than strings
 */
public class Transaction {

    public String hash, from, amount;
    public Instant when;
    //public Amount amount;

    public Transaction() {
        this(null, null, null, null);
    }

    public Transaction(Instant when, String amount, String from, String hash) {
        this.when = when;
        this.amount = amount;
        this.from = from;
        this.hash = hash;
    }

    public Instant when() {
        return when;
    }

    public String amount() {
        return amount;
    }

    public String from() {
        return from;
    }

    public String hash() {
        return hash;
    }

    public String whenZ() {
        return when.toString();
    }

}
