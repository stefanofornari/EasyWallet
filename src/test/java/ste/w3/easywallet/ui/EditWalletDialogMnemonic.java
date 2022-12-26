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

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import ste.xtest.concurrent.WaitFor;


/**
 *
 */
public class EditWalletDialogMnemonic extends ApplicationTest implements Labels, TestingConstants, TestingUtils {

    private EditWalletDialog dialog;

    private final String TEST_MNEMONIC = "word01 word02 word03 word04 word05 word06 word07 word08 word09 word10 word11 word12";
    private final Wallet WALLET = new Wallet(ADDRESS3);

    @Override
    public void start(Stage stage) throws Exception {
        WALLET.mnemonicPhrase = TEST_MNEMONIC;
        Pane mainWindow = new BorderPane();
        dialog = new EditWalletDialog(mainWindow, WALLET);
        ((EditWalletController)dialog.controller).mnemonicPane.setExpanded(true);

        showInStage(stage, mainWindow);

        dialog.show();
    }

    @Test
    public void edit_wallet_dialog_mnemonic_widgets() {
        Then.then(lookup("#mnemonicText")).hasOneWidget();
        Then.then(lookup(LABEL_MNEMONIC_TITLE)).hasWidgets();
        Then.then(lookup(LABEL_MNEMONIC_PHRASE)).hasWidgets();
        Then.then(lookup(TEST_MNEMONIC)).hasWidgets();
        Then.then(lookup("#searchButton").queryButton()).isEnabled();

        //lookup("#mnemonicText").queryAs(TextField.class).clear();

        clickOn("#mnemonicText");
        push(KeyCode.CONTROL, KeyCode.A); type(KeyCode.BACK_SPACE);
        Then.then(lookup(LABEL_MNEMONIC_PHRASE_HINT)).hasWidgets();
        Then.then(lookup("#searchButton").queryButton()).isDisabled();

    }

    @Test
    public void valid_mnemonic_enables_search_button() {
        Button search = lookup("#searchButton").queryButton();

        clickOn("#mnemonicText");
        clear(); write("two words");
        Then.then(search).isDisabled();
        clear(); write(LABEL_MNEMONIC_PHRASE_HINT);
        Then.then(search).isEnabled();
    }

    @Test
    public void search_finds_a_private_key() {
        clickOn("#mnemonicText");
        lookup("#mnemonicText").queryAs(TextField.class).setText(LABEL_MNEMONIC_PHRASE_HINT);
        clickOn("#searchButton");

        new WaitFor(250, () -> {
            return PRIVATE_KEY6.equals(lookup("#keyText").queryAs(TextField.class).getText());
        });
    }

    @Test
    public void search_button_turns_into_cancel_and_back_to_search() {
        Platform.runLater(() -> {
            ((EditWalletController)dialog.controller).wallet(new Wallet(ADDRESS7));
        }); waitForFxEvents();

        lookup("#mnemonicText").queryAs(TextField.class).setText(LABEL_MNEMONIC_PHRASE_HINT);
        clickOn("#searchButton");
        Then.then(lookup("#searchButton").queryButton()).isDisabled().isInvisible();
        sleep(50);
        clickOn("#cancelButton");

        new WaitFor(50, () -> {
            Button b = lookup("#searchButton").queryButton();
            return b.isVisible() && !b.isDisabled();
        });
    }

    // --------------------------------------------------------- private methods

    private void clear() {
        push(KeyCode.CONTROL, KeyCode.A); type(KeyCode.BACK_SPACE);
    }

}