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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * TODO: remove coin
 */
@DatabaseTable(tableName = "TRANSACTIONS")
public class Transaction {
    @DatabaseField(id = true, width = 64)
    public String hash;
    @DatabaseField(canBeNull = false, width=40)
    public String from;
    @DatabaseField(canBeNull = false, width=40)
    public String to;
    @DatabaseField(canBeNull = false, dataType = DataType.BIG_DECIMAL, columnDefinition="numeric(27,18)" )
    public BigDecimal amount;
    @DatabaseField(canBeNull = false)
    public Date when;
    @DatabaseField(width=12)
    public String coin;

    public Transaction() {
        this(null, null, null, null, null, null);
    }

    public Transaction(Date when, Coin coin, BigDecimal amount, String from, String to, String hash) {
        this.when = when;
        this.coin = (coin != null) ? coin.symbol : null;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.hash = hash;
    }

    public Date when() {
        return when;
    }

    public BigDecimal amount() {
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

    public String coin() {
        return coin;
    }
}
