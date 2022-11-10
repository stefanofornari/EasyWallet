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

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 */
public class PreferencesTest {

    @Test
    public void get_url_combines_endpoint_and_appkey() {
        final String ENDPOINT1 = "https://somewere.com";
        final String ENDPOINT2 = "https://nowhere.com/";
        final String APPKEY1 = "thekey1";
        final String APPKEY2 = "thekey2";

        Preferences p = new Preferences();

        p.endpoint = ENDPOINT1; p.appkey = APPKEY1;
        then(p.url()).isEqualTo(ENDPOINT1 + "/" + APPKEY1);

        p.endpoint = ENDPOINT2; p.appkey = APPKEY2;
        then(p.url()).isEqualTo(ENDPOINT2 + APPKEY2);
    }
}
