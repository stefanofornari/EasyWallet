/*
 * Copyright (C) 2022 Stefano Fornari.
 * Licensed under the EUPL-1.2 or later.
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

import org.apache.commons.lang3.StringUtils;


/**
 *
 *
 */
public class Preferences {
    public String   endpoint = "";
    public String   appkey   = "";
    public Wallet[] wallets  = {};
    public Coin[]   coins    = {};
    public String   db       = "";

    public String url() {
        return StringUtils.removeEnd(endpoint, "/") +
               "/" +
               StringUtils.removeStart(appkey, "/");
    }
}
