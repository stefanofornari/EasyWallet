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
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class EditWalletControllerTest
extends ApplicationTest
implements Labels, TestingConstants, TestingUtils {

    final Wallet WALLET = new Wallet(ADDRESS3);

    private EditWalletController controller = null;

    @Override
    public void start(Stage stage) throws IOException {
        WALLET.privateKey = PRIVATE_KEY3;
        EditWalletDialog dialog = new EditWalletDialog(new Pane(), WALLET);
        controller = dialog.controller;

        showInStage(stage, dialog.getOwnerNode());

        dialog.show();
    }

    @Test
    public void initial_state() {
        Then.then(controller.keyText).hasText(PRIVATE_KEY3);
        Then.then(controller.okButton).isEnabled();
        then(controller.mnemonicPane.isExpanded()).isFalse();
    }

    @Test
    public void IllegalStateException_is_wallet_is_null_on_onOk() {
        controller.wallet(null);
        try {
            controller.onOk();
            fail("illegal state not detected");
        } catch (IllegalStateException x) {
            then(x).hasMessage("wallet shall be set before onOk can be invoked");
        }
    }

    @Test
    public void set_and_get_wallet() {
        final Wallet W = new Wallet(WALLET1);
        final String MNEMONIC = "this is a fake mnemonic phrase";
        final String KEY = "this is a fake private key";

        W.mnemonicPhrase = MNEMONIC; W.privateKey = KEY;

        controller.wallet(null);
        then(controller.wallet()).isNull();
        controller.wallet(W);
        then(controller.wallet()).isSameAs(W);
        then(W.address).isEqualTo(WALLET1);
        then(W.mnemonicPhrase).isEqualTo(MNEMONIC);
        then(W.privateKey).isEqualTo(KEY);
    }

    @Test(timeout = 5000)
    public void onSearch_finds_valid_key() {
        final Wallet W3 = new Wallet(ADDRESS3);
        final Wallet W6 = new Wallet(ADDRESS6);

        Platform.runLater(() -> {
            controller.wallet(W6);
            controller.mnemonicText.setText(MNEMONIC3);
            controller.onSearch(null);
        }); waitForFxEvents();

        waitFor(controller.searchTask);
        Then.then(controller.keyText).hasText(PRIVATE_KEY6);

        Platform.runLater(() -> {
            controller.wallet(W3);
            controller.mnemonicText.setText(LABEL_MNEMONIC_PHRASE_HINT);
            controller.onSearch(null);
        }); waitForFxEvents();

        waitFor(controller.searchTask);
        Then.then(controller.keyText).hasText(PRIVATE_KEY3);
    }
}
