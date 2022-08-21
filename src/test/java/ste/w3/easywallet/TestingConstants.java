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
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote" #1
    //
    public final String PRIVATE_KEY6 = "82b4cd6699cc1aee53b492598def7833a5ca8aae948f817c325548cb3e62c610";
    //
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote" #3
    //
    public final String PRIVATE_KEY7 = "a850017f110e9030d806b749a58bf15e150fa1e0edf492593af1d28e9390c796";
    //
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote" #14
    //
    public final String PRIVATE_KEY3 = "c8f12c80b8c0325bb15aa8546f7b0bea133c884da3cfb6f2096368d94192cb37";
    //
    // Derived by mnemonic phrase "alert record income curve mercy tree heavy loan hen recycle mean devote" #3825
    //
    public final String PRIVATE_KEY8 = "21de2c51db9ac0cf564140c6e803036ffc22c0879a21fdf57866aca969aacdbc";


    public final String ADDRESS1 = "c2a6927e5e2f27e5fc7d2611cb0246fb3151f034";
    public final String ADDRESS2 = "496ef9de509d5d4b3f48f33eb75e55c4b3005dc7";
    public final String ADDRESS3 = "1489a7dd02ca2294ed999cfc175050c852851dec"; // associated to PRIVATE_KEY3
    public final String ADDRESS4 = "00000000219ab540356cbb839cbe05303d7705fa";
    public final String ADDRESS5 = "c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
    public final String ADDRESS6 = "b24f4ad87c027f05c58a71eed50193364c1c4a22"; // associated to PRIVATE_KEY6
    public final String ADDRESS8 = "e85d9ca59611232428e1bb26b6b1bef7c4d32329"; // associated to PRIVATE_KEY8

    public final String MNEMONIC1 = "wild quiz always market robust board acid wild quiz always market robust board acid";
    public final String MNEMONIC2 = "update elbow source spin squeeze horror world become oak assist bomb nuclear";
    public final String MNEMONIC3 = Labels.LABEL_MNEMONIC_PHRASE_HINT;

    //
    // derived from MNEMONIC1
    //
    public final String SEED1 = "35347e2a0610d295f2c6651caa69f95cbff130008b19d5acf93828611e36750d8eeffc89d5ae2c6922a78bedd69e6c366eb131e5d6e33028edb45fac8a73483b";
    //
    // derived from MNEMONIC2
    //
    public final String SEED2 = "77e6a9b1236d6b53eaa64e2727b5808a55ce09eb899e1938ed55ef5d4f8153170a2c8f4674eb94ce58be7b75922e48e6e56582d806253bd3d72f4b3d896738a4";
}
