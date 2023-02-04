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
package ste.w3.easywallet.ui.demo;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.LogManager;

/**
 *
 */
public class LoggingDemo {
    public static final String JDBC_DRIVER_CLASS = "org.hsqldb.jdbc.JDBCDriver";
    public static final String JDBC_CONNECTION_STRING = "jdbc:hsqldb:mem:testdb";

    @DatabaseField(id = true, width = 64)
    public final String id;
    @DatabaseField(canBeNull = false, width=40)
    public final String first;
    @DatabaseField(canBeNull = false, width=40)
    public final String last;

    public LoggingDemo() {
        id = "one";
        first = "first name";
        last = "last name";
    }

    public static void main(String... args) throws Exception {
        Class.forName(JDBC_DRIVER_CLASS);

        System.getProperties().put("com.j256.simplelogger.backend", "JAVA_UTIL");

        LogManager.getLogManager().readConfiguration(new FileInputStream("src/main/resources/logging.properties"));

        ConnectionSource db = new JdbcConnectionSource(JDBC_CONNECTION_STRING);
        Dao<LoggingDemo, String> dao = DaoManager.createDao(db, LoggingDemo.class);

        TableUtils.dropTable(dao, true);
        TableUtils.createTable(dao);

        dao.create(new LoggingDemo());

        db.close();
    }

}
