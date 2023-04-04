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
package ste.w3.easywallet.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.InitialContext;
import org.apache.commons.lang3.StringUtils;
import ste.w3.easywallet.Block;
import ste.w3.easywallet.ManagerException;
import ste.w3.easywallet.Preferences;

/**
 *
 */
public class BlocksManager {
    public final Dao<Block, String> blocks;

    public BlocksManager() throws ManagerException {
        try (ConnectionSource db = db()) {
            blocks = DaoManager.createDao(db, Block.class);
            TableUtils.createTableIfNotExists(db, Block.class);
        } catch (Exception x) {
            throw new ManagerException("error trying to access transactions data", x);
        }

    }

    public Block mostRecent() throws ManagerException {
        try {
            return blocks.queryBuilder().orderBy("number", false).queryForFirst();
        } catch (SQLException x) {
            throw new ManagerException("error retriving most recent block", x);
        }
    }

    public void add(Block b) throws ManagerException {
        try {
            blocks.create(b);
        } catch(SQLException x) {
            throw new ManagerException("error adding a block", x);
        }
    }

    public List<Block> all() throws ManagerException {
        try {
            return blocks.queryBuilder().query();
        } catch (SQLException x) {
            throw new ManagerException("error retrieving blocks data", x);
        }
    }

    // --------------------------------------------------------- private methods

    private JdbcConnectionSource db() throws ConfigurationException {
        JdbcConnectionSource db = null;

        try {
            Preferences preferences = (Preferences)new InitialContext().lookup("root/preferences");
            if (StringUtils.isBlank(preferences.db)) {
                throw new ConfigurationException("db connection string is missing");
            }
            db = new JdbcConnectionSource(preferences.db);
        } catch (Exception x) {
            x.printStackTrace();
            throw new ConfigurationException("db connection string is missing");
        }

        return db;
    }

}
