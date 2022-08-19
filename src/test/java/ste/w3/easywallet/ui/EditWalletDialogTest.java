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
import java.util.function.Consumer;
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


/**
 *
 */
public class EditWalletDialogTest extends ApplicationTest implements Labels, TestingConstants, TestingUtils {

    private EditWalletDialog dialog;

    private static final Wallet WALLET = new Wallet(WALLET1);


    @Override
    public void start(Stage stage) throws Exception {
        Pane mainWindow = new BorderPane();
        dialog = new EditWalletDialog(WALLET);

        showInStage(stage, mainWindow);

        dialog.show();
    }

    @Test
    public void edit_wallet_dialog_widgets() {
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
            }
        );

        Then.then(lookup(".mfx-text-field")).hasNWidgets(2);
    }

    @Test
    public void controller() {
        then(dialog.controller).isNotNull();
    }

    /*
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
    */
}
