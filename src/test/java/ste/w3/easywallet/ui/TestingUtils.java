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
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

/**
 *
 */
public interface TestingUtils {

    default public void showInStage(Stage stage, Pane pane) {
        //
        // We set the roor as opposed to create a Scene any time for
        // performance reasons
        //

        Scene s = stage.getScene();
        if (s == null) {
            stage.setScene(new Scene(pane));
        } else {
            s.setRoot(pane);
        }
        stage.show();
    }

    default public void showInStageLater(Stage stage, Pane pane) {
        Platform.runLater(() -> {
            showInStage(stage, pane);
        }); waitForFxEvents();
    }

    default public <T> T getController(Pane pane) {
    return (T)pane.getUserData();
}

}
