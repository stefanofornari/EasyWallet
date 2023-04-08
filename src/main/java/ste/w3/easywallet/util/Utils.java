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
package ste.w3.easywallet.util;

import com.google.gson.Gson;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ste.w3.easywallet.Wallet;
import static ste.w3.easywallet.Constants.UTC_TIMESTAMP_FORMAT;
import static ste.w3.easywallet.util.DaemonThreadFactory.FACTORY;

/**
 * TODO: rename to Utils (or delete it)
 */
public class Utils {

    public static void printWallets(Wallet[] wallets) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(wallets));
    }

    public static String ex(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("s can not be null");
        }

        return (s.startsWith("0x")) ? s : ("0x" + s);
    }

    public static String unex(final String s) {
        if (s == null) {
            return null;
        }

        return (s.startsWith("0x")) ? s.substring(2) : s;
    }

    /**
     * Format the given epoch timestamp as YYYYMMDDhhmmss'UTC'
     *
     * @param seconds the epoch timestamp in UNIX epoch seconds
     *
     * @return timestamp formatted
     */
    public static String ts(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("seconds can not be negative");
        }

        return String.format(UTC_TIMESTAMP_FORMAT, Instant.ofEpochSecond(seconds).atZone(ZoneId.of("UTC")));
    }

    /**
     * Format the given date as YYYYMMDDhhmmss'UTC'
     *
     * @param date the date
     *
     * @return the date formatted
     */
    public static String ts(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date can not be null");
        }

        return ts(date.getTime()/1000);
    }

    public static ExecutorService newSingleDaemonThreadExecutor() {
        //
        // I do not know how to bugfree code this...
        //
        return Executors.newSingleThreadExecutor(FACTORY);
    }
}
