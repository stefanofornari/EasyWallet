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

import ste.w3.easywallet.TestingUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import ste.xtest.reflect.PrivateAccess;


/**
 *
 */
public class EditWalletDialogTest extends ApplicationTest implements Labels, TestingConstants, TestingUtils {

    private EditWalletDialog dialog;

    private static final Wallet WALLET = new Wallet(ADDRESS3);

    @Override
    public void start(Stage stage) throws Exception {
        Pane mainWindow = new BorderPane();
        dialog = new EditWalletDialog(mainWindow, WALLET);

        showInStage(stage, mainWindow);

        dialog.show();
    }

    @Test
    public void edit_wallet_dialog_widgets_and_controller() {
        //
        // The OK button is disabled until there is a valid address
        //
        then(lookup(".button").queryAllAs(MFXButton.class)).satisfiesExactlyInAnyOrder(
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).hasText(LABEL_BUTTON_OK);
                    then(b.getStyleClass()).contains("primary-button");
                }
            },
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).hasText(LABEL_BUTTON_CANCEL);
                }
            },
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).hasText(LABEL_BUTTON_SEARCH);
                }
            },
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).hasText(LABEL_BUTTON_CANCEL);
                }
            }
        );

        Then.then(lookup(".mfx-text-field")).hasNWidgets(2);
        Then.then(lookup(String.format(LABEL_EDIT_WALLET_PRIVATE_KEY_TITLE, WALLET.address))).hasWidgets();

        then(dialog.controller).isNotNull();
    }

    @Test
    public void no_more_than_64_chars() {
        TextField text = lookup(".mfx-text-field").queryAs(TextField.class);
        text.setText("0000000000000000000000000000000000000000");
        clickOn(".mfx-text-field"); type(KeyCode.DIGIT0, 30);
        Then.then(text).hasText("0000000000000000000000000000000000000000000000000000000000000000"); // only 64 chars
    }

    @Test
    public void valid_private_key_enables_ok_button() {
        TextField text = lookup(".mfx-text-field").queryAs(TextField.class);
        Button b = lookup(".primary-button").queryButton();

        clickOn(".mfx-text-field");
        type(KeyCode.DELETE); Then.then(b).isDisabled();
        text.setText(PRIVATE_KEY3.substring(2)); Then.then(b).isDisabled();
        text.setText(PRIVATE_KEY3); Then.then(b).isEnabled();
        type(KeyCode.END, KeyCode.BACK_SPACE); Then.then(b).isDisabled();
        type(KeyCode.Z); Then.then(b).isDisabled();
        type(KeyCode.BACK_SPACE); type(KeyCode.COLON); Then.then(b).isDisabled();
    }

     @Test
    public void pressing_ok_returns_the_updated_wallet1() {
        final Wallet[] wrapper = new Wallet[1];

        TextField address = lookup(".mfx-text-field").queryAs(TextField.class);

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                System.out.println(w.address);
                wrapper[0] = w; return null;
            }
        };

        address.setText(PRIVATE_KEY3);
        clickOn(".primary-button");
        then(wrapper[0]).isSameAs(WALLET);
        then(wrapper[0].address).isEqualTo(ADDRESS3);
        then(wrapper[0].privateKey).isEqualTo(PRIVATE_KEY3);
    }

    @Test
    public void pressing_ok_returns_the_address2() throws Exception {
        final Wallet W = new Wallet(ADDRESS6);
        PrivateAccess.setInstanceValue(dialog.controller, "wallet", W);

        final Wallet[] wrapper = new Wallet[1];

        TextField key = lookup(".mfx-text-field").queryAs(TextField.class);
        key.setText(PRIVATE_KEY6);

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                wrapper[0] = w; return null;
            }
        };

        clickOn(".primary-button");
        then(wrapper[0]).isSameAs(W);
        then(wrapper[0].address).isEqualTo(ADDRESS6);
        then(wrapper[0].privateKey).isEqualTo(PRIVATE_KEY6);
    }

    @Test
    public void pressing_cancel_does_not_invoke_onOk() {
        boolean[] wrapper = new boolean[1];

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                wrapper[0] = true; return null;
            }
        };

        lookup(".mfx-text-field").queryAs(TextField.class).setText(PRIVATE_KEY1);
        clickOn(LABEL_BUTTON_CANCEL);
        then(wrapper[0]).isFalse();
    }

    @Test
    public void do_nothing_when_onOk_is_null() {
        clickOn(".mfx-text-field");
        lookup(".mfx-text-field").queryAs(TextField.class).setText(PRIVATE_KEY1);
        clickOn(LABEL_BUTTON_OK);
        Then.then(lookup(".mfx-dialog")).hasNoWidgets();
    }

    @Test
    public void close_on_ESC() {
        then(dialog.isShowing()).isTrue();
        type(KeyCode.ESCAPE);
        then(dialog.isShowing()).isFalse();
    }
}