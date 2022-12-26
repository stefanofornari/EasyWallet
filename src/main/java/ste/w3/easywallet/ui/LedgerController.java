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

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
import java.time.Instant;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import ste.w3.easywallet.Transaction;
import ste.w3.easywallet.Wallet;

/**
 *
 */
public class LedgerController extends EasyWalletDialogController<Void> {

    @FXML
    protected MFXPaginatedTableView transactions;

    public LedgerController(final MFXGenericDialog dialog) {
        super(dialog);
    }

    @FXML
    public void initialize() {
        setupTable();

        transactions.autosizeColumnsOnInitialization();

	When.onChanged(transactions.currentPageProperty())
            .then((oldValue, newValue) -> transactions.autosizeColumns())
            .listen();
    }

    private void setupTable() {
        MFXTableColumn<Transaction> whenColumn = new MFXTableColumn<>("when", false, Comparator.comparing(Transaction::when));
        MFXTableColumn<Transaction> amountColumn = new MFXTableColumn<>("amount", false, Comparator.comparing(Transaction::amount));
        MFXTableColumn<Transaction> fromColumn = new MFXTableColumn<>("from", false, Comparator.comparing(Transaction::from));

        whenColumn.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::when));
        amountColumn.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::amount));
        fromColumn.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::from));

        transactions.getTableColumns().addAll(whenColumn, amountColumn, fromColumn);
        transactions.getFilters().addAll(
                        new StringFilter<>("when", Transaction::whenZ),
                        new StringFilter<>("amount", Transaction::amount),
                        new StringFilter<>("from", Transaction::from)
                        //new EnumFilter<>("State", Device::getState, Device.State.class)
        );

        ObservableList<Transaction> data = FXCollections.observableArrayList();

        for (int i=1; i<=50; ++i) {
            data.add(
                new Transaction(
                    Instant.parse(String.format("2022-11-10T10:%02d:00Z", i)),
                    String.format("%1$02d.%1$02d", i),
                    String.format("12345678901234567890123456789012345678%02d",i),
                    String.format("hahs%02d",i)
                )
            );
        }
        transactions.setItems(data);
    }

    @Override
    protected Void onOk() {
        return null;
    }

}
