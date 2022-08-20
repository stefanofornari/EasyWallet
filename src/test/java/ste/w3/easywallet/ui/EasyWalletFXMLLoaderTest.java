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

import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import javafx.scene.layout.Pane;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EasyWalletFXMLLoaderTest extends ApplicationTest {

    private final EasyWalletFXMLLoader instance = new EasyWalletFXMLLoader();

    private final static Wallet[] I = new Wallet[0]; // invlid wallets


    @Test
    public void loaded_main_window_pane_has_controller() {
        Pane pane = instance.loadMainWindow(new EasyWalletMain());
        then(pane.getUserData()).isNotNull().isInstanceOf(EasyWalletMainController.class);
    }

    @Test
    public void loaded_card_pane_has_controller_and_id() {
        Pane pane = instance.loadCardPane(I, new Wallet(TestingConstants.WALLET1));
        then(pane.getUserData()).isNotNull().isInstanceOf(WalletCardController.class);
        then(pane.getId()).isEqualTo(TestingConstants.WALLET1);

        then(pane).isNotNull();
        pane = instance.loadCardPane(I, new Wallet(TestingConstants.WALLET2));
        then(pane.getId()).isEqualTo(TestingConstants.WALLET2);
    }

    @Test
    public void loaded_edit_wallet_content_has_controller() {
        final Wallet W = new Wallet(TestingConstants.WALLET1);

        Pane pane = instance.loadEditWalletDialogContent(MFXGenericDialogBuilder.build().get());

        then(pane).isNotNull();
        EditWalletController controller = (EditWalletController)pane.getUserData();
        then(controller).isNotNull().isInstanceOf(EditWalletController.class);
        then(controller.wallet()).isNull();
    }

}
