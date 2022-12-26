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
import java.util.List;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ste.w3.easywallet.EasyWalletException;
import static ste.w3.easywallet.Labels.ERR_NETWORK;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class EasyWalletMainController {

    private final EasyWalletMain main;

    @FXML
    protected BorderPane easyWalletMain;

    @FXML
    protected Pane walletsPane;

    @FXML
    protected MFXButton addButton;

    @FXML
    protected MFXButton refreshButton;

    @FXML
    protected MFXButton closeErrorButton;

    @FXML
    protected Pane errorPane;

    @FXML
    protected Label errorLabel;

    public EasyWalletMainController(EasyWalletMain main) {
        this.main = main;
    }

    @FXML
    public void initialize() {
        //
        // errorPane is in the fxml so that it is clear where it is, but we do
        // not show it at the beginning
        //
        easyWalletMain.setTop(null);
        //
        walletsPane.getChildren().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                refreshButton.disableProperty().set(
                    ((ObservableList<Node>)o).isEmpty()
                );
            }
        });
        for (Wallet wallet : main.getPreferences().wallets) {
            addCard(wallet);
        }

        closeErrorButton.getRippleGenerator().setClipSupplier(
            () -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(40).build(closeErrorButton)
        );
    }

    // --------------------------------------------------------- private methods

    @FXML
    private void onAddWallet(ActionEvent event) {
        AddWalletDialog dialog = new AddWalletDialog(easyWalletMain, main.getPreferences().wallets);
        dialog.onOk = new Function<Wallet, Void>() {
            @Override
            public Void apply(Wallet wallet) {
                main.addWallet(wallet);
                addCard(wallet);

                return null;
            }
        };
        dialog.showAndWait();
    }

    @FXML
    private void onCloseError(ActionEvent event) {
        easyWalletMain.setTop(null);
    }

@FXML
    private void onRefresh(ActionEvent event) {
        Platform.runLater(() -> {
            WalletManager wm = main.getWalletManager();
            for (Node n: walletsPane.getChildren()) {
                WalletCardController cardController = (WalletCardController)n.getUserData();
                try {
                    wm.balance(cardController.wallet, main.getPreferences().coins);
                    cardController.refreshBalance();
                    main.savePreferences();
                } catch (EasyWalletException x) {
                    showError(x.getMessage());
                    //
                    // TODO: do something different here, maybe log?
                    //
                    x.printStackTrace();
                }

            }
        });
    }

    private void addCard(Wallet wallet) {
        Pane card = new EasyWalletFXMLLoader().loadCardPane(main.getPreferences().wallets, wallet);
        WalletCardController controller = (WalletCardController)card.getUserData();
        controller.onDelete = new Function<String, Void>() {
            @Override
            public Void apply(String t) {
                List<Node> children = walletsPane.getChildren();
                for (Node n: children) {
                    if (n.getId().equals(wallet.address)) {
                        main.deleteWallet(wallet);
                        children.remove(n);
                        break;
                    }
                }
                return null;
            }
        };

        walletsPane.getChildren().add(card);
    }

    private void showError(String err) {
        errorLabel.setText(
            String.format("%s (%s)", ERR_NETWORK, err));
        easyWalletMain.setTop(errorPane);
    }

}
