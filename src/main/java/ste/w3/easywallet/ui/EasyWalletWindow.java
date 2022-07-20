/*
 * EasyWallet
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
public class EasyWalletWindow extends Pane {

    public final String ID = "wallets";

    public EasyWalletWindow(Wallet[] wallets) {
        setId(ID);
        setUserData(wallets);

        try {
            Pane card = FXMLLoader.load(
                EasyWalletMain.class.getResource("/fxml/WalletCard.fxml"),
                null, /* resourceBundle */
                null, /* builderFactory */
                new Callback<Class<?>, Object>() { /* controllerFactory */
                    @Override
                    public Object call(Class<?> p) {
                        if (wallets.length > 0) {
                            return new WalletController(wallets[0]);
                        } else {
                            return new WalletController();
                        }
                    }
                }
            );

            getChildren().add(card);
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
