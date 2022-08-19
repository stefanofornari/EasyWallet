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

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import java.util.Set;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import ste.w3.easywallet.TestingConstants;
import ste.w3.easywallet.Wallet;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class AddWalletControllerTest extends ApplicationTest implements TestingConstants {

    @Test
    public void empty_invalid_wallets() throws Exception {
        MFXGenericDialog dialog = MFXGenericDialogBuilder.build().get();
        AddWalletController c = new AddWalletController(dialog);

        c.setInvalidWallets(new Wallet[0]);
        then((Set)PrivateAccess.getInstanceValue(c, "invalidAddresses")).isEmpty();

        c.setInvalidWallets(null);
        then((Set)PrivateAccess.getInstanceValue(c, "invalidAddresses")).isEmpty();
    }

    @Test
    public void invalid_wallets() throws Exception {
        MFXGenericDialog dialog = MFXGenericDialogBuilder.build().get();
        AddWalletController c = new AddWalletController(dialog);

        c.setInvalidWallets(new Wallet[] {
            new Wallet(WALLET1), new Wallet(WALLET2)
        });
        then((Set)PrivateAccess.getInstanceValue(c, "invalidAddresses"))
            .containsExactlyInAnyOrder(WALLET1, WALLET2);

    }

}
