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
 *
 */
public interface TestingConstants {

    public final String WALLET1 = "1234567890123456789012345678901234567890";
    public final String WALLET2 = "0123456789012345678901234567890123456789";

    //
    // Randomly generated address:
    //
    // var rng = Random.secure();
    //    EthPrivateKey random = EthPrivateKey.createRandom(rng);
    //    print(bytesToHex(random.privateKey));
    //    print((await random.extractAddress()).hex);
    //
    public final String PRIVATE_KEY1 = "8a2b2d41febc2bef749ecec009b86e5fa18753439b28789658eb7b411397abb6";
    public final String PRIVATE_KEY2 = "436804c64fea7474fc184d88f8219a3a72c6a9c26321e53babd3c4a8775ed88f";
    //
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote"
    //
    public final String PRIVATE_KEY3 = "c8f12c80b8c0325bb15aa8546f7b0bea133c884da3cfb6f2096368d94192cb37";
    //
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote" #1
    //
    public final String PRIVATE_KEY6 = "82b4cd6699cc1aee53b492598def7833a5ca8aae948f817c325548cb3e62c610";
    public final String ADDRESS1 = "0xc2a6927e5e2f27e5fc7d2611cb0246fb3151f034";
    public final String ADDRESS2 = "0x496ef9de509d5d4b3f48f33eb75e55c4b3005dc7";
    public final String ADDRESS3 = "0x1489a7dd02ca2294ed999cfc175050c852851dec"; // associated to PRIVATE_KEY3
    public final String ADDRESS4 = "0x00000000219ab540356cbb839cbe05303d7705fa";
    public final String ADDRESS5 = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
    public final String ADDRESS6 = "0xb24f4ad87c027f05c58a71eed50193364c1c4a22"; // associated to PRIVATE_KEY6

}
