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
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import ste.w3.easywallet.Labels;

/**
 *
 */
public abstract class EasyWalletDialog extends MFXStageDialog implements Labels {
    public final EasyWalletDialogController controller;

    public EasyWalletDialog(final Pane owner, final String title) {
        super(
            MFXGenericDialogBuilder.build()
                .setHeaderText(title)
                .setShowClose(false)
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .get()
        );

        setScrimOwner(true);
        setScrimStrength(0.30);
        setOverlayClose(true);
        // ---
        // At the moment centerInOwnernode shows a bad effect appearing in the
        // center of the screan and then in the center of the owner. Let's
        // disable the effect for now waiting for MaterialFX to fix it
        // (related bug: https://github.com/palexdev/MaterialFX/issues/227)
        //
        setCenterInOwnerNode(false);
        // ---

        MFXGenericDialog dialog = (MFXGenericDialog)getContent();

        Pane content = content();
        dialog.setContent(content);
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

        controller = (EasyWalletDialogController)content.getUserData();
        controller.okButton.setDisable(true);
        controller.okButton.getStyleClass().add("primary-button");
        controller.cancelButton.setCancelButton(true);
        controller.cancelButton.onActionProperty().setValue((event) -> close());

        dialog.addActions(
            Map.entry(controller.cancelButton, e -> {
                close();
            })
        );

        initModality(Modality.APPLICATION_MODAL);
        setDraggable(true);
        setOwnerNode(owner);
    }

    abstract Pane content();

}
