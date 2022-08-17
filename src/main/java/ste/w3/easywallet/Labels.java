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
package ste.w3.easywallet;

/**
 *
 */
public interface Labels {
    public final String LABEL_OK = "OK";
    public final String LABEL_CANCEL = "CANCEL";

    public final String LABEL_ADD_WALLET_DIALOG_TITLE = "Add a public wallet";
    public final String LABEL_ADDRESS = "Insert the 20 hex bytes public address:";
    public final String LABEL_ADDRESS_HINT = "eg: 00000000219ab540356cBB839Cbe05303d7705Fa";

    public final String ERR_NETWORK = "I am unable to retrieve the information from the provider, check that your internet connection is working and the endpoint is correct";
}
