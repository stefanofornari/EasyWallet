/*
 * EasyWallet
 * ----------
 *
 * Copyright (C) 2023 Stefano Fornari. Licensed under the
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

import java.text.SimpleDateFormat;

/**
 *
 */
public class Constants {

    public static final String EASYWALLET_LOG_NAME = "ste.easywallet";

    public static final String UTC_TIMESTAMP_FORMAT = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tZ";

    public static final String LOG_BLOCK_FORMAT_OK = "block %s %s %s ok";
    public static final String LOG_BLOCK_FORMAT_KO = "block %s %s %s ko block older than %s";
    public static final String LOG_TRANSACTION_FORMAT_OK = "transaction 0x%s ok 0x%s 0x%s %s %s";
    public static final String LOG_TRANSACTION_FORMAT_KO = "transaction 0x%s ko %s";
}
