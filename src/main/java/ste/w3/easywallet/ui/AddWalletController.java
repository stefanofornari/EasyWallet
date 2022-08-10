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

import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.web3j.crypto.WalletUtils;

/**
 *
 */
public class AddWalletController {

    final MFXGenericDialog dialog;

    @FXML
    private MFXTextField addrText;



    public AddWalletController(final MFXGenericDialog dialog) {
        this.dialog = dialog;
    }

    @FXML
    public void initialize() {
        addrText.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
        addrText.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldValue, Object newValue) {
                boolean valid = WalletUtils.isValidAddress(addrText.getText());

                addrText.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !valid);
                getOkButton().disableProperty().set(!valid);
            }
        });
    }

    // --------------------------------------------------------- private methods

    private Button getOkButton() {
        ObservableList<Node> actions = ((Pane)dialog.getBottom()).getChildren();
        return (Button)actions.get(1);
    }
}
