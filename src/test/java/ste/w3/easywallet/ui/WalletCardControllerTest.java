/*
 * Copyright (C) 2022 Stefano Fornari.
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
import java.util.function.Function;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class WalletCardControllerTest extends ApplicationTest implements TestingConstants {

    final Wallet WALLET = new Wallet(WALLET1);

    private WalletCardController controller = null;

    @Override
    public void start(Stage stage) throws IOException {
        Pane card = new EasyWalletFXMLLoader().loadCardPane(WALLET);
        stage.setScene(new Scene(card));
        controller = (WalletCardController)card.getUserData();
        stage.show();
    }

    @Test
    public void onDelete_when_delete_button_is_pressed() throws Exception {
        final boolean[] TEST = new boolean[] {false};

        controller.onDelete = new Function<String, Void>() {
            @Override
            public Void apply(String wallet) {
                TEST[0] = true; return null;
            }
        };

        clickOn("mfx-delete");

        then(TEST[0]).isTrue();
    }

    @Test
    public void nothing_when_delete_button_is_pressed_and_onDelete_is_null() throws Exception {
        clickOn("mfx-delete");
        //
        // nothing happens
        //
    }
}