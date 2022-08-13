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
import java.util.function.Function;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;


/**
 *
 */
public class AddWalletDialogTest extends ApplicationTest implements Labels {

    AddWalletDialog dialog;

    @Override
    public void start(Stage stage) throws Exception {
        Pane mainWindow = new BorderPane();

        stage.setScene(new Scene(mainWindow));
        stage.show();

        dialog = new AddWalletDialog(mainWindow);

        dialog.show();
    }

    @Before
    public void before() {

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
                    Then.then(b).hasText(LABEL_OK).isDisabled();
                    then(b.getStyleClass()).contains("primary-button");
                }
            },
            new Consumer<MFXButton>() {
                @Override
                public void accept(MFXButton b) {
                    Then.then(b).isEnabled().hasText(LABEL_CANCEL);
                }
            }
        );

        Then.then(lookup(".mfx-text-field")).hasOneWidget();
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
        Button b = lookup(".primary-button").queryAs(Button.class);
        clickOn(".mfx-text-field"); type(KeyCode.DIGIT0);
        Then.then(b).isDisabled();
        type(KeyCode.DIGIT0); Then.then(b).isDisabled();
        type(KeyCode.DIGIT0, 38); Then.then(b).isEnabled();
        type(KeyCode.BACK_SPACE); Then.then(b).isDisabled();
        type(KeyCode.Z); Then.then(b).isDisabled();
        type(KeyCode.BACK_SPACE); type(KeyCode.COLON); Then.then(b).isDisabled();
    }

    @Test
    public void pressing_ok_returns_the_address1() {
        final String[] ret = new String[1];

        TextField address = lookup(".mfx-text-field").queryAs(TextField.class);

        dialog.onOk = new Function<>() {
            public Void apply(String a) {
                ret[0] = a; return null;
            }
        };

        clickOn(".mfx-text-field");
        address.setText(TestingConstants.WALLET1);
        clickOn(".primary-button");
        then(ret[0]).isEqualTo(TestingConstants.WALLET1);
    }

    @Test
    public void pressing_ok_returns_the_address2() {
        final String[] ret = new String[1];

        TextField address = lookup(".mfx-text-field").queryAs(TextField.class);

        dialog.onOk = new Function<>() {
            public Void apply(String a) {
                ret[0] = a; return null;
            }
        };

        clickOn(".mfx-text-field");
        address.setText(TestingConstants.WALLET2);
        clickOn(".primary-button");
        then(ret[0]).isEqualTo(TestingConstants.WALLET2);
    }

    @Test
    public void pressing_cancel_returns_null() {
        boolean[] test = new boolean[1];

        dialog.onOk = new Function<>() {
            public Void apply(String a) {
                test[0] = true; return null;
            }
        };

        clickOn(".mfx-text-field");
        lookup(".mfx-text-field").queryAs(TextField.class).setText(TestingConstants.WALLET2);
        clickOn(LABEL_CANCEL);
        then(test[0]).isFalse();
    }

    @Test
    public void do_nothing_when_onOk_is_null() {
        clickOn(".mfx-text-field");
        lookup(".mfx-text-field").queryAs(TextField.class).setText(TestingConstants.WALLET2);
        clickOn(LABEL_OK);
        //
        // end with no exceptions...
        //
    }

    /*

    expect(
      find.descendant(of: dialog, matching: find.byType(Radio<AddWalletBy>)
      ), findsWidgets
    );
*/

}
