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

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class EasyWalletMainController {

    private final EasyWalletMain main;

    @FXML
    Pane easyWalletMain;

    @FXML
    Pane walletsPane;

    @FXML
    Button addButton;

    @FXML
    Button refreshButton;


    public EasyWalletMainController(EasyWalletMain main) {
        this.main = main;
    }

    @FXML
    public void initialize() {
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
    }

    @FXML
    protected void onAddWallet(ActionEvent event) {
        AddWalletDialog dialog = new AddWalletDialog(easyWalletMain);
        dialog.onOk = new Function<String, Void>() {
            @Override
            public Void apply(String address) {
                Wallet wallet = new Wallet(address);
                main.addWallet(wallet);
                addCard(wallet);

                return null;
            }
        };
        dialog.showAndWait();
    }

    @FXML
    protected void onRefresh(ActionEvent event) {
        //
        // TODO: use Platform.runLater
        //
        Platform.runLater(() -> {
            WalletManager wm = main.getWalletManager();
            for (Node n: walletsPane.getChildren()) {
                WalletCardController cardController = (WalletCardController)n.getUserData();
                try {
                    wm.balance(cardController.wallet);
                    cardController.refreshBalance();
                } catch (IOException x) {
                    //
                    // TODO: handle the exception
                    //
                    x.printStackTrace();
                }

            }
        });
    }

    // --------------------------------------------------------- private methods

    private void addCard(Wallet wallet) {
        Pane card = new EasyWalletFXMLLoader().loadCardPane(wallet);
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

}
