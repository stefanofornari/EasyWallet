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
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EasyWalletFXMLLoader {

    public Pane loadMainWindow(Wallet[] wallets) {
        return loadPane(
                "/fxml/EasyWalletMain.fxml",
                new Callback<Class<?>, Object>() {
                    /* controllerFactory */
                    @Override
                    public Object call(Class<?> p) {
                        EasyWalletMainController controller = new EasyWalletMainController();
                        controller.wallets.addAll(wallets);

                        return controller;
                    }
                });
    }

    public Pane loadCardPane(Wallet wallet) {
        return loadPane(
                "/fxml/WalletCard.fxml",
                new Callback<Class<?>, Object>() {
                    /* controllerFactory */
                    @Override
                    public Object call(Class<?> p) {
                        return new WalletCardController(wallet);
                    }
                });
    }

    public Pane loadPane(String resource, Callback<Class<?>, Object> controllerFactory) {
        try {
            final Object[] controllerWrapper = new Object[1];

            Pane pane = FXMLLoader.load(
                EasyWalletMain.class.getResource(resource),
                null, /* resourceBundle */
                null, /* builderFactory */
                new Callback<Class<?>, Object>() {
                    @Override
                    public Object call(Class<?> p) {
                        return (controllerWrapper[0] = controllerFactory.call(p));

                    }
                }
            );

            pane.setUserData(controllerWrapper[0]);

            return pane;
        } catch (IOException x) {
            //
            // TODO: better handling of this exception
            throw new RuntimeException("unable to load FXML " + resource, x);
        }
    }
}
