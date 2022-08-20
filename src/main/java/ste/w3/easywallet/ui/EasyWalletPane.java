/*
 * EasyWallet
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
package ste.w3.easywallet.ui;

import javafx.scene.layout.Pane;
import ste.w3.easywallet.Wallet;

/**
 * TODO: use the controller!
 */
public class EasyWalletPane extends Pane {

    public final String ID = "wallets";

    public EasyWalletPane(Wallet[] wallets) {
        setId(ID);

        if (wallets != null) {
            for(Wallet wallet: wallets) {
                getChildren().add(
                    new EasyWalletFXMLLoader().loadCardPane(wallets, wallet)
                );
            }
        }
    }
}
