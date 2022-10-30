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
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import static ste.w3.easywallet.Coin.ETH;
import static ste.w3.easywallet.Coin.STORJ;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class WalletCardController {

    @FXML
    private Pane walletCard;

    @FXML
    private MFXButton editButton;

    @FXML
    private MFXButton deleteButton;

    @FXML
    private Label address;

    @FXML
    private Label labelBalance;

    public final Wallet wallet;
    public final Wallet[] invalidWallets;

    public Function<String, Void> onDelete;

    public WalletCardController(Wallet[] invalidWallets, Wallet wallet) {
        this.invalidWallets = invalidWallets;
        this.wallet = wallet;
    }

    @FXML
    public void initialize() {
        if (wallet == null) {
            return;
        }

        walletCard.setId(wallet.address);

        address.setText("0x" + wallet.address);
        labelBalance.setText(getBalance());
        editButton.getRippleGenerator().setClipSupplier(
            () -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(40).build(editButton)
        );
        deleteButton.getRippleGenerator().setClipSupplier(
            () -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(40).build(deleteButton)
        );
    }

    @FXML
    public void editWallet() {
        EditWalletDialog dialog = new EditWalletDialog((Pane)walletCard.getScene().getRoot(), wallet);
        dialog.showAndWait();
    }

    @FXML
    public void deleteWallet() {
        if (onDelete != null) {
            onDelete.apply(wallet.address);
        }
    }

    public void refreshBalance() {
        labelBalance.setText(getBalance());
    }

    // --------------------------------------------------------- private methods

    private String getBalance() {
        return String.format(
            "ETH %s - STORJ %s",
            (wallet.balance(ETH) == null) ? "0.0" : String.valueOf(wallet.balance(ETH)),
            (wallet.balance(STORJ) == null) ? "0.0" : String.valueOf(wallet.balance(STORJ))
        );
    }


}
