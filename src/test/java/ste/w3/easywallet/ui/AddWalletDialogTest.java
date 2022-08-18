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
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;


/**
 *
 */
public class AddWalletDialogTest extends ApplicationTest implements Labels, TestingConstants, TestingUtils {

    private final Wallet[] INVALID_WALLETS = new Wallet[] {
        new Wallet(ADDRESS1), new Wallet(ADDRESS2)
    };

    private AddWalletDialog dialog;


    @Override
    public void start(Stage stage) throws Exception {
        Pane mainWindow = new BorderPane();
        dialog = new AddWalletDialog(mainWindow, INVALID_WALLETS);

        showInStage(stage, mainWindow);

        dialog.show();
    }

    @Test
    public void add_wallet_dialog_widgets() {
        //
        // The OK button is diantil there is a valid address
        //
        then(lookup(".button").queryAllAs(MFXButton.class)).satisfiesExactlyInAnyOrder(
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).hasText(LABEL_BUTTON_OK).isDisabled();
                    then(b.getStyleClass()).contains("primary-button");
                }
            },
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).isEnabled().hasText(LABEL_BUTTON_CANCEL);
                }
            }
        );

        Then.then(lookup(".mfx-text-field")).hasOneWidget();
        Then.then(lookup(".mfx-radio-button")).hasNWidgets(2);
        then(lookup(".mfx-radio-button").queryAllAs(RadioButton.class).toArray(new RadioButton[0])[0].isDisable()).isFalse();

        //
        // public address
        //
        MFXTextField text = lookup(".mfx-text-field").queryAs(MFXTextField.class);
        then(text.getTextLimit()).isEqualTo(40);
        then(text.getFloatingText()).isEqualTo(LABEL_ADDRESS);
        then(text.getPromptText()).isEqualTo(LABEL_ADDRESS_HINT);
    }

    @Test
    public void switch_labels_clicking_on_radios() {
        MFXTextField text = lookup(".mfx-text-field").queryAs(MFXTextField.class);

        clickOn(LABEL_RADIO_PRIVATE_KEY);
        then(text.getTextLimit()).isEqualTo(64);
        then(text.getFloatingText()).isEqualTo(LABEL_PRIVATE_KEY);
        then(text.getPromptText()).isEqualTo(LABEL_PRIVATE_KEY_HINT);

        clickOn(LABEL_RADIO_PUBLIC_ADDRESS);
        then(text.getTextLimit()).isEqualTo(40);
        then(text.getFloatingText()).isEqualTo(LABEL_ADDRESS);
        then(text.getPromptText()).isEqualTo(LABEL_ADDRESS_HINT);
    }

    @Test
    public void no_more_than_40_chars() {
        clickOn(".mfx-text-field"); type(KeyCode.DIGIT0, 50);
        Then.then(
            lookup(".mfx-text-field").queryAs(TextField.class)
        ).hasText("0000000000000000000000000000000000000000"); // only 40 chars
    }

    @Test
    public void valid_address_enables_ok_button() {
        Button b = lookup(".primary-button").queryButton();
        clickOn(".mfx-text-field"); type(KeyCode.DIGIT0);
        Then.then(b).isDisabled();
        type(KeyCode.DIGIT0); Then.then(b).isDisabled();
        type(KeyCode.DIGIT0, 38); Then.then(b).isEnabled();
        type(KeyCode.BACK_SPACE); Then.then(b).isDisabled();
        type(KeyCode.Z); Then.then(b).isDisabled();
        type(KeyCode.BACK_SPACE); type(KeyCode.COLON); Then.then(b).isDisabled();
    }

    @Test
    public void valid_private_key_enables_ok_button() {
        Button b = lookup(".primary-button").queryButton();
        TextField f = lookup("#text").queryAs(TextField.class);

        clickOn(LABEL_RADIO_PRIVATE_KEY);
        clickOn(".mfx-text-field"); type(KeyCode.DIGIT0);
        Then.then(b).isDisabled();
        type(KeyCode.DIGIT0); Then.then(b).isDisabled();
        f.setText(PRIVATE_KEY3.substring(2)); waitForFxEvents(); Then.then(b).isDisabled();
        f.setText(PRIVATE_KEY3); waitForFxEvents(); Then.then(b).isEnabled();
        type(KeyCode.END, KeyCode.BACK_SPACE); Then.then(b).isDisabled();
        type(KeyCode.Z); Then.then(b).isDisabled();
        type(KeyCode.BACK_SPACE); type(KeyCode.COLON); Then.then(b).isDisabled();
    }

    @Test
    public void pressing_ok_returns_the_address1() {
        final String[] ret = new String[1];

        TextField address = lookup(".mfx-text-field").queryAs(TextField.class);

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                ret[0] = w.address; return null;
            }
        };

        clickOn(".mfx-text-field");
        address.setText(WALLET1);
        clickOn(".primary-button");
        then(ret[0]).isEqualTo(WALLET1);
    }

    @Test
    public void pressing_ok_returns_the_address2() {
        final String[] ret = new String[1];

        TextField address = lookup(".mfx-text-field").queryAs(TextField.class);

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                ret[0] = w.address; return null;
            }
        };

        clickOn(".mfx-text-field"); waitForFxEvents();
        address.setText(WALLET2);
        clickOn(".primary-button"); waitForFxEvents();
        then(ret[0]).isEqualTo(WALLET2);
    }

    @Test
    public void pressing_cancel_returns_null() {
        boolean[] test = new boolean[1];

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                test[0] = true; return null;
            }
        };

        clickOn(".mfx-text-field");
        lookup(".mfx-text-field").queryAs(TextField.class).setText(WALLET2);
        clickOn(LABEL_BUTTON_CANCEL);
        then(test[0]).isFalse();
    }

    @Test
    public void do_nothing_when_onOk_is_null() {
        clickOn(".mfx-text-field");
        lookup(".mfx-text-field").queryAs(TextField.class).setText(WALLET2);
        clickOn(LABEL_BUTTON_OK);
        //
        // end with no exceptions...
        //
    }

    @Test
    public void do_not_add_a_wallet_twice() {
        TextField t = lookup(".mfx-text-field").queryAs(TextField.class);
        clickOn(".mfx-text-field");
        t.setText(ADDRESS1);
        Then.then(lookup(LABEL_BUTTON_OK).queryButton()).isDisabled();
        t.setText(ADDRESS2);
        Then.then(lookup(LABEL_BUTTON_OK).queryButton()).isDisabled();
    }

    @Test
    public void add_wallet_by_private_key1() {
        final String[] test = new String[1];

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                test[0] = w.address; return null;
            }
        };

        clickOn(LABEL_RADIO_PRIVATE_KEY); waitForFxEvents();
        lookup(".mfx-text-field").queryAs(TextField.class).setText(PRIVATE_KEY3);
        clickOn(".primary-button"); waitForFxEvents();

        then(test[0]).isEqualTo(ADDRESS3);
    }

    @Test
    public void add_wallet_by_private_key2() {
        final String[] ret = new String[1];

        dialog.onOk = new Function<>() {
            public Void apply(Wallet w) {
                ret[0] = w.address; return null;
            }
        };

        clickOn(LABEL_RADIO_PRIVATE_KEY); waitForFxEvents();
        lookup(".mfx-text-field").queryAs(TextField.class).setText(PRIVATE_KEY6);
        clickOn(".primary-button"); waitForFxEvents();

        then(ret[0]).isEqualTo(ADDRESS6);
    }

}
