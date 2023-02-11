/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2023 Stefano Fornari. Licensed under the
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
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class ConfirmationDialog extends OkCancelDialogBase {
    public ConfirmationDialog(Pane owner, Wallet wallet, String title, String text) {
        super(owner, wallet, title);

        controller.okButton.setDisable(false); controller.okButton.setText("YES");
        controller.okButton.setDisable(false); controller.cancelButton.setText("NO");

        ((Labeled)getContent().lookup("#DIALOGTEXT")).setText(text);
    }

    @Override
    public Pane content() {
        //
        // NOTE: this is going to be called by super constructor
        //
        Label text = new Label();
        text.setId("DIALOGTEXT");
        text.setWrapText(true);
        text.setTextAlignment(TextAlignment.LEFT);
        text.setMaxSize(350, 200);
        StackPane p = new StackPane(text);
        p.setUserData(
            new OkCancelControllerBase((MFXGenericDialog)getContent())
        );
        return p;
    }
}
