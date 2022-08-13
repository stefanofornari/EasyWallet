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
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.commons.io.FileUtils;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.PreferencesManager;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EasyWalletMainController implements InvalidationListener {

    private final EasyWalletMain main;

    @FXML
    Pane easyWalletMain;

    @FXML
    Pane walletsPane;

    public final WalletList wallets = new WalletList();

    public EasyWalletMainController(EasyWalletMain main) {
        this.main = main;
        wallets.addListener(this);
        wallets.addAll(main.getPreferences().wallets);
    }

    @FXML
    public void initialize() {
    }

    @Override
    public void invalidated(Observable o) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (walletsPane == null) {
                    return;
                }
                ObservableList children = walletsPane.getChildren();
                children.clear();

                for (Wallet wallet : wallets) {
                    children.add(
                        new EasyWalletFXMLLoader().loadCardPane(wallet)
                    );
                }
            }
        });
    }

    @FXML
    protected void onAddWallet(ActionEvent event) {
        AddWalletDialog dialog = new AddWalletDialog(easyWalletMain);
        dialog.showAndWait();

        Wallet wallet = new Wallet(dialog.ret.get());
        addWallet(wallet);

        //
        // TODO: use a callback and remove ret from AddWalletDialog
        //

        //if (dialog.ret.get() != null) {
            walletsPane.getChildren().add(
                new EasyWalletFXMLLoader().loadCardPane(wallet)
            );
            //
            // TODO: move the code below in a savePreferences() method in EasyWalletMain
            //
            try {
                PreferencesManager pm = new PreferencesManager();
                FileUtils.write(main.getConfigFile(), pm.toJSON(main.getPreferences()), "UTF-8");
            } catch (IOException x) {
                x.printStackTrace();
            }

        //}

    }

    // --------------------------------------------------------- private methods

    private void addWallet(Wallet wallet) {
        Preferences p = main.getPreferences();

        Wallet[] newList = new Wallet[p.wallets.length+1];
        System.arraycopy(p.wallets, 0, newList, 0, p.wallets.length);
        newList[p.wallets.length] = wallet;
        p.wallets = newList;
    }

}
