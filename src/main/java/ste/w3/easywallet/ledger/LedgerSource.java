package ste.w3.easywallet.ledger;

import ste.w3.easywallet.data.Order;
import ste.w3.easywallet.data.TableSourceSorting;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.TransactionsManager;
import ste.w3.easywallet.Wallet;

/**
 *
 * TODO: return only transactions of a given wallet
 *
 *
 */
public class LedgerSource {
    final public ObservableList<Transaction> page = FXCollections.observableArrayList();
    final public Wallet wallet;

    private static final long DEFAULT_PAGE_SIZE = 1000;

    protected TableSourceSorting sorting = null;
    protected long startFrom = 0;
    protected long pageSize = DEFAULT_PAGE_SIZE;

    public LedgerSource(final Wallet wallet) {
        this.wallet = wallet;
    }

    public void sortBy(final TableSourceSorting s) {
        sorting = (s.order() == Order.NONE)? null : s;
        reset();
        fetch();
    }

    public TableSourceSorting sorting() {
        return sorting;
    }

    public void fetch() {
        try {
            TransactionsManager transactionManager = new TransactionsManager();

            List rows = transactionManager.get(wallet, sorting, startFrom, pageSize);
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
