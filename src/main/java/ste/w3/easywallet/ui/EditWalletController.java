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
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EditWalletController {

    @FXML
    protected MFXButton searchButton;

    @FXML
    protected MFXTextField mnemonicText;

    @FXML
    protected MFXTextField keyText;

    protected MFXButton okButton = new MFXButton();
    protected MFXButton cancelButton;


    protected final Wallet wallet;

    public EditWalletController(Wallet wallet) {
        this.wallet = wallet;
    }

    @FXML
    public void initialize() {
        okButton.setDisable(true);
    }

    // --------------------------------------------------------- private methods

}
