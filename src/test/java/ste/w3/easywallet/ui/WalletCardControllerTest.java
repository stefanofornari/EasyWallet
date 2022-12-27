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

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.naming.Context;
import javax.naming.InitialContext;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Test;
import org.testfx.assertions.api.Then;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import ste.w3.easywallet.Amount;
import ste.w3.easywallet.Coin;
import ste.w3.easywallet.Labels;
import ste.w3.easywallet.Preferences;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class WalletCardControllerTest
extends ApplicationTest
implements Labels, TestingConstants, TestingUtils {

    final Wallet WALLET = new Wallet(WALLET1);

    private WalletCardController controller = null;

    @Before
    public void before() throws Exception {
        Context ctx = new InitialContext();

        //synchronized (ctx) {
        try {
            ctx.destroySubcontext("root");
        } catch (Exception x) {}

        Preferences preferences = new Preferences();
        preferences.coins = new Coin[] {ETH, STORJ};

        ctx = ctx.createSubcontext("root");
        ctx.bind("preferences", preferences);
    //}
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane card = new EasyWalletFXMLLoader().loadCardPane(new Wallet[0], WALLET);
        controller = (WalletCardController)card.getUserData();

        showInStage(stage, card);
    }

    @Test
    public void initiale_state() throws Exception {
        NodeQuery q = lookup(".icon-button");
        MFXButton[] buttons = q.queryAll().toArray(new MFXButton[0]);

        //
        // I could not find a better way to check buttons have the proper ripple
        //
        then(buttons).hasSize(3).allSatisfy(new Consumer<MFXButton>() {
            @Override
            public void accept(MFXButton b) {
                then(b.getRippleGenerator().getClipSupplier().getClass().getName())
                    .doesNotStartWith(MFXCircleRippleGenerator.class.getName());
            }

        });

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
        clickOn("#deleteButton");
        //
        // nothing happens
        //
    }

    @Test
    public void show_private_key_dialog_when() {
        clickOn("#editButton");
        Then.then(lookup(String.format(LABEL_EDIT_WALLET_PRIVATE_KEY_TITLE, WALLET.address))).hasWidgets();
    }

    @Test
    public void show_coins_balance() throws Exception {
        WALLET.balance(
            new Amount(ETH, new BigDecimal("10.0"))
        );
        Platform.runLater(() -> {
            controller.refreshBalance();
        }); waitForFxEvents();

        Then.then(lookup("ETH 10.0")).hasWidgets();

        WALLET.balance(
            new Amount(STORJ, new BigDecimal("15.15"))
        );
        Platform.runLater(() -> {
            controller.refreshBalance();
        }); waitForFxEvents();

        Then.then(lookup("ETH 10.0 - STORJ 15.15")).hasWidgets();
    }

    @Test
    public void show_ledger_dialog_when() {
        clickOn("#ledgerButton");
        Then.then(lookup(String.format(LABEL_LEDGER_DIALOG_TITLE, WALLET1))).hasWidgets();
    }

}
