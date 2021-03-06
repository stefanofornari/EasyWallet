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
import javafx.scene.layout.StackPane;
import ste.w3.easywallet.Preferences;

/**
 *
 */
public class EasyWalletWindow extends Pane {

    public EasyWalletWindow(Preferences preferences) {
        try {
            Pane content = FXMLLoader.load(EasyWalletMain.class.getResource("/fxml/EasyWalletMain.fxml"));
            StackPane pane = (StackPane)content.getChildren().get(0);
            pane.getChildren().add(0, new EasyWalletPane(preferences.wallets));
        } catch (IOException x) {
            x.printStackTrace(); // TODO: general management of such exceptions
            throw new IllegalStateException("Error loading EasyWalletWindow FXML", x);
        }
    }

}
