package ste.w3.easywallet.ledger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ste.w3.easywallet.Transaction;

/**
 *
 * TODO: SQL connection handling
 *
 *
 */
public class LedgerSource {
    final public ObservableList<Transaction> page = FXCollections.observableArrayList();

    private static final long DEFAULT_PAGE_SIZE = 1000;

    protected TableSourceSorting sorting = null;
    protected long startFrom = 0;
    protected long pageSize = DEFAULT_PAGE_SIZE;

    public void sortBy(final TableSourceSorting s) {
        sorting = (s.order() == Order.NONE)? null : s;
        reset();
        fetch();
    }

    public TableSourceSorting sorting() {
        return sorting;
    }

    public void fetch() {
        try (ConnectionSource db = new JdbcConnectionSource("jdbc:hsqldb:mem:testdb")) {
            Dao<Transaction, String> dao = DaoManager.createDao(db, Transaction.class);

            QueryBuilder qb = dao.queryBuilder().offset(startFrom).limit(pageSize);
            if (sorting != null) {
                qb.orderBy(sorting.column(), sorting.order() == Order.ASCENDING);
            }
            List rows = qb.query();
            page.addAll(rows);

            startFrom += rows.size();
        } catch (Exception x) {
             // TODO: error handling
             x.printStackTrace();
        }
    }

    public long pageSize() {
        return pageSize;
    }

    public void pageSize(final long pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        this.pageSize = pageSize;
    }

    // --------------------------------------------------------- private methods

    private void reset() {
        page.clear(); startFrom = 0l;
    }
}
