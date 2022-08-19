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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import static ste.w3.easywallet.TestingConstants.WALLET1;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EditWalletControllerTest
extends ApplicationTest
implements Labels, TestingConstants, TestingUtils {

    final Wallet WALLET = new Wallet(WALLET1);

    private EditWalletController controller = null;

    @Override
    public void start(Stage stage) throws IOException {
        Pane pane = new EasyWalletFXMLLoader().loadEditWalletDialogContent(WALLET);
        controller = (EditWalletController)pane.getUserData();

        showInStage(stage, pane);
    }


    @Test
    public void initial_state() {
        System.out.println(controller);
        Then.then(controller.okButton).isDisabled();

    }

}
