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
package ste.w3.easywallet.ui;

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import javafx.scene.layout.Pane;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class AddWalletDialog extends OkCancelDialogBase {
    public AddWalletDialog(Pane owner, Wallet[] invalidWallets) {
        super(owner, null, LABEL_ADD_WALLET_DIALOG_TITLE);
        ((AddWalletController)controller).setInvalidWallets(invalidWallets);
    }

    @Override
    protected Pane content() {
        return new EasyWalletFXMLLoader().loadAddWalletDialogContent((MFXGenericDialog)getContent());
    }
}
