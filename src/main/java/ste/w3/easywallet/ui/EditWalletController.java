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
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import static io.github.palexdev.materialfx.validation.Validated.INVALID_PSEUDO_CLASS;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import org.apache.commons.lang.StringUtils;
import ste.w3.easywallet.BIP32Utils;
import ste.w3.easywallet.Wallet;
import ste.w3.easywallet.WalletManager;

/**
 *
 */
public class EditWalletController extends WalletDialogController {

    @FXML
    protected MFXButton searchButton;

    @FXML
    protected MFXButton searchCancelButton;

    @FXML
    protected MFXTextField mnemonicText;

    @FXML
    protected MFXTextField keyText;

    @FXML
    protected TitledPane mnemonicPane;

    protected Task searchTask;

    private Wallet wallet;

    public EditWalletController(final MFXGenericDialog dialog) {
        super(dialog);
    }

    @FXML
    public void initialize() {
        super.initialize();
        mnemonicPane.setExpanded(false);

        mnemonicPane.expandedProperty().addListener( (obs, oldValue, newValue) -> {
            Platform.runLater( () -> {
                mnemonicPane.requestLayout();
                mnemonicPane.getScene().getWindow().sizeToScene();
            });
        });

        keyText.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldValue, Object newValue) {
                boolean valid = false;
                String t = keyText.getText();

                if (t.length() == 64) {
                    try {
                        t = WalletManager.fromPrivateKey(t).address;
                        valid = true;
                    } catch (NumberFormatException x) {
                    }
                }
                keyText.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !valid);
                okButton.setDisable(!valid);
            }
        });

        mnemonicText.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldValue, Object newValue) {
                final String text = ((String)newValue);

                boolean valid = (StringUtils.split(text).length == 12);

                mnemonicText.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !valid);
                searchButton.setDisable(!valid);
            }
        });
    }

    public Wallet wallet() {
        return wallet;
    }

    public void wallet(final Wallet wallet) {
        this.wallet = wallet;
        if (wallet != null) {
            if (wallet.privateKey != null) {
                keyText.setText(wallet.privateKey);
            }
            if (wallet.mnemonicPhrase != null) {
                mnemonicText.setText(wallet.mnemonicPhrase);
            }
        }
    }

    @FXML
    protected void onSearchCancel(ActionEvent e) {
        searchTask.cancel();
    }

    @FXML
    protected void onSearch(ActionEvent e) {

        //
        // todo: prevent to start a task if one is already running
        //
        new Thread(searchTask = new KeySearchTask(this)).start();
    }

    /**
     * Invoked when the ok button is pressed.
     *
     * @return the updated wallet
     *
     * @throws IllegalStateException if wallet has not been set
     */
    @Override
    protected Wallet onOk() throws IllegalStateException {
        if (wallet == null) {
            throw new IllegalStateException("wallet shall be set before onOk can be invoked");
        }
        wallet.privateKey = keyText.getText();
        return wallet;
    }

    // --------------------------------------------------------- private methods

    private void swapButtons() {
        searchButton.setDisable(!searchButton.isDisabled());
        searchButton.setVisible(!searchButton.isVisible());
        searchCancelButton.setDisable(!searchCancelButton.isDisabled());
        searchCancelButton.setVisible(!searchCancelButton.isVisible());
    }


    // ----------------------------------------------------------- KeySearchTask

    protected static class KeySearchTask extends Task<Void>{
        private final EditWalletController controller;

        public KeySearchTask(EditWalletController controller) {
            this.controller = controller;
        }

        @Override
        protected Void call() throws Exception {
            BIP32Utils BIP32 = new BIP32Utils();

            BIP32.privateKeyFromMnemonicAndAddress(
                controller.mnemonicText.getText(), controller.wallet.address,
                new Function<>() {
                    @Override
                    public Boolean apply(String key) {
                        updateMessage(key);
                        return !isCancelled();
                    }
                }
            );
            return null;
        }

        @Override
        protected void running() {
            controller.keyText.textProperty().bind(controller.searchTask.messageProperty());
            controller.swapButtons();
        }

        @Override
        protected void succeeded() {
            cleanup();
        }

        @Override
        protected void failed() {
            cleanup();
        }

        @Override
        protected void cancelled() {
            cleanup();
        }

        private void cleanup() {
            controller.swapButtons();
            controller.keyText.textProperty().unbind();
        }

    };
}
