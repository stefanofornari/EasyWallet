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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import java.util.Map;
import java.util.function.Function;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class AddWalletDialog extends MFXStageDialog implements Labels {

    public Function<String, Void> onOk;

    public AddWalletDialog(Pane owner, Wallet[] invalidWallets) {
        super(
            MFXGenericDialogBuilder.build()
                .setHeaderText(LABEL_ADD_WALLET_DIALOG_TITLE)
                .setShowClose(false)
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .get()
        );

        setScrimOwner(false);
        setOverlayClose(true);
        // ---
        // At the moment centerInOwnernode shows a bad effect appearing in the
        // center of the screan and then in the center of the owner. Let's
        // disable the effect for now waiting for MaterialFX to fix it
        // (related bug: https://github.com/palexdev/MaterialFX/issues/227)
        //
        setCenterInOwnerNode(false);
        // ---

        MFXButton okButton = new MFXButton(LABEL_OK);
        okButton.disableProperty().set(true);
        okButton.getStyleClass().add("primary-button");
        MFXButton cancelButton = new MFXButton(LABEL_CANCEL);

        MFXGenericDialog dialog = (MFXGenericDialog)getContent();

        dialog.setContent(new EasyWalletFXMLLoader().loadAddWalletDialogContent(dialog, invalidWallets));
        dialog.setStyle("-fx-background-color: blue;");
        dialog.addActions(
            Map.entry(cancelButton, e -> {
                close();
            }),
            Map.entry(okButton, e -> {
                if (onOk != null) {
                    onOk.apply(((TextField)getScene().lookup("#addrText")).getText());
                }
                close();
            })
        );
        dialog.alwaysOnTopProperty().bind(alwaysOnTopProperty());
        dialog.setOnAlwaysOnTop(event -> setAlwaysOnTop(!dialog.isAlwaysOnTop()));
	dialog.setOnMinimize(event -> setIconified(true));
	dialog.setOnClose(event -> close());
        dialog.getStylesheets().add(
          EasyWalletMain.class.getResource("/css/easywallet.css").toExternalForm()
        );
        dialog.setStyle(
            "-fx-border-color: -ew-primary-color;"
        );

        initModality(Modality.APPLICATION_MODAL);
        setDraggable(true);
        setOwnerNode(owner);
    }

}
