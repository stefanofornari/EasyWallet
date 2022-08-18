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
import java.util.HashSet;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import org.web3j.crypto.WalletUtils;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class AddWalletController implements Labels {

    private final MFXGenericDialog dialog;
    private final Set<String> invalidAddresses = new HashSet();

    @FXML
    private MFXTextField text;

    @FXML
    private RadioButton addressRadio;

    @FXML
    private RadioButton keyRadio;


    public AddWalletController(final MFXGenericDialog dialog, final Wallet[] invalidWallets) {
        this.dialog = dialog;

        for (Wallet w: invalidWallets) {
            invalidAddresses.add(w.address);
        }
    }

    @FXML
    public void initialize() {
        text.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
        text.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldValue, Object newValue) {
                boolean valid = true;
                String address = text.getText();

                if (addressRadio.isSelected()) {
                    valid = WalletUtils.isValidAddress(address);
                    if (valid) {
                        valid = !invalidAddresses.contains(address);
                    }
                    text.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !valid);
                }
                getOkButton().disableProperty().set(!valid);
            }
        });
    }

    @FXML
    public void onPrivateKey() {
        text.setTextLimit(64);
        text.setFloatingText(LABEL_PRIVATE_KEY);
        text.setPromptText(LABEL_PRIVATE_KEY_HINT);
    }

    @FXML
    public void onPublicAddress() {
        text.setTextLimit(40);
        text.setFloatingText(LABEL_ADDRESS);
        text.setPromptText(LABEL_ADDRESS_HINT);
    }


    public String onOk() {
        //
        // TODO: this should return the wallet, not only the address...
        //
        return addressRadio.isSelected() ? text.getText() : WalletManager.fromPrivateKey(text.getText()).address;
    }


    // --------------------------------------------------------- private methods

    private Button getOkButton() {
        ObservableList<Node> actions = ((Pane)dialog.getBottom()).getChildren();
        return (Button)actions.get(1);
    }
}
