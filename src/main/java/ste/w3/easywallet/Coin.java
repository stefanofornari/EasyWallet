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

import org.apache.commons.lang3.StringUtils;



/**
 * TODO: turn it into record
 * TODO: use coin as its own symbol only
 */
public class Coin {
    public final String contract, symbol, name;
    public final int decimals;

    /**
     *
     * @param symbol - coin's symbol - not null
     * @param name - coin's display name - not null
     * @param contract - coin's contract - may be null if this is the main coin for the network
     * @param decimals - coin's significant decimals - > 0
     *
     * @throws IllegalArgumentException in case of invalid arguments
     *
     * TODO: arguments check
     */
    public Coin(final String symbol, final String name, final String contract, final int decimals)
    throws IllegalArgumentException {
        if (StringUtils.isBlank(symbol)) {
            throw new IllegalArgumentException("symbol can not be blank");
        } else {
            this.symbol = symbol;
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name can not be blank");
        } else {
            this.name = name;
        }

        if ((contract != null) && StringUtils.isBlank(contract)) {
            throw new IllegalArgumentException("contract can not be blank");
        } else {
            this.contract = contract;
        }

        if (decimals < 1) {
            throw new IllegalArgumentException("decimals can not be less than 1");
        } else {
            this.decimals = decimals;
        }
    }

    /**
     * Same as this(symbol, name, null, decimals), this represents the main coin
     * for a network.
     *
     * @param symbol
     * @param name
     * @param decimals
     */
    public Coin(final String symbol, final String name, final int decimals) {
        this(symbol, name, null, decimals);
    }

    /**
     * Same as this(symbol, symbol, null, decimals)
     */
    public Coin(final String symbol, final int decimals) {
        this(symbol, symbol, null, decimals);
    }
}

