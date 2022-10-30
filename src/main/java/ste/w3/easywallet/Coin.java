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

/**
 * ERC20 Contracts:
 *
 * Ethereum
 * StorjToken - STORJ - 0xb64ef51c888972c908cfacf59b47c1afbc0ab8ac
 *
 * Polygon
 * Golem Network Token - GLM: 0x0B220b82F3eA3B7F6d9A1D8ab58930C064A2b5Bf
 * StorjToken - STORJ - 0xd72357dAcA2cF11A5F155b9FF7880E595A3F5792
 *
 */
public enum Coin {
    ETH(null, "ETH", "Ether", 18),
    STORJ("0xb64ef51c888972c908cfacf59b47c1afbc0ab8ac", "STORJ", "StorjToken - Ethereum network", 8),
    GLM("0x0B220b82F3eA3B7F6d9A1D8ab58930C064A2b5Bf", "GLM", "Golem Network Token (PoS)", 18);

    Coin(String contract, String symbol, String name, int decimals) {
        this.contract = contract;
        this.symbol  = symbol;
        this.name     = name;
        this.decimals = decimals;
    }

    public final String contract, symbol, name;
    public final int decimals;
}

