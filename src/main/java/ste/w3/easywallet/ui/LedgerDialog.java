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
import java.util.Map;
import javafx.scene.layout.Pane;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class LedgerDialog extends EasyWalletDialog implements Labels {
    public LedgerDialog(final Pane owner, final Wallet wallet) {
        super(owner, String.format(LABEL_LEDGER_DIALOG_TITLE, wallet.address));
        setMinWidth(800); setMinHeight(600);

        controller.cancelButton.setText(LABEL_BUTTON_CLOSE);
        controller.cancelButton.getStyleClass().add("primary-button");
        controller.okButton.setVisible(false);

        setMinWidth(800); setMinHeight(600);
    }

    protected Pane content() {
        return new EasyWalletFXMLLoader().loadLedgerDialogContent((MFXGenericDialog)getContent());
    }
}
