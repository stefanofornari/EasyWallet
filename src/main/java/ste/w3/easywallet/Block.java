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
import java.math.BigInteger;
import java.util.Date;

/**
 * TODO: remove coin
 */
@DatabaseTable(tableName = "BLOCKS")
public class Block {
    @DatabaseField(id = true, width = 64)
    public String hash;
    @DatabaseField(canBeNull = false, dataType = DataType.BIG_INTEGER, columnDefinition="numeric(12,0)")
    public BigInteger number;
    @DatabaseField(canBeNull = false)
    public Date when;

    public Block() {
        this(null, null, null);
    }

    public Block(Date when, BigInteger number, String hash) {
        this.when = when;
        this.number = number;
        this.hash = hash;
    }
}
