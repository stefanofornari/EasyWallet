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
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import org.web3j.crypto.WalletUtils;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class AddWalletController extends WalletDialogController {

    private final Set<String> invalidAddresses = new HashSet();

    @FXML
    private MFXTextField text;

    @FXML
    private RadioButton addressRadio;

    public AddWalletController(final MFXGenericDialog dialog) {
        super(dialog);
    }

    public void setInvalidWallets(Wallet[] wallets) {
        if (wallets != null) {
            for (Wallet w: wallets) {
                invalidAddresses.add(w.address);
            }
        }
    }

    @FXML
    public void initialize() {
        super.initialize();
        text.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
        text.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldValue, Object newValue) {
                boolean valid = false;
                String t = text.getText();

                if (addressRadio.isSelected()) {
                    valid = WalletUtils.isValidAddress(t);
                } else {
                    if (t.length() == 64) {
                        try {
                            t = WalletManager.fromPrivateKey(t).address;
                            valid = true;
                        } catch (NumberFormatException x) {
                        }
                    }
                }
                if (valid) {
                    valid = !invalidAddresses.contains(t);
                }
                text.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !valid);
                okButton.disableProperty().set(!valid);
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


    public Wallet onOk() {
        return addressRadio.isSelected() ? new Wallet(text.getText()) : WalletManager.fromPrivateKey(text.getText());
    }
}
