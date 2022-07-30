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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import ste.w3.easywallet.Wallet;

/**
 * TODO: fire event on single modification methods set, add, ...
 */
class WalletList extends AbstractList<Wallet> implements ObservableList<Wallet> {
    private final List<Wallet> store = new ArrayList();
    private final List<InvalidationListener> listeners = new ArrayList();

    @Override
    public boolean addAll(Wallet... wallets) {
        boolean ret = store.addAll(Arrays.asList(wallets));

        listeners.forEach((listener) -> {
            listener.invalidated(this);
        });

        return ret;
    }

    @Override
    public boolean setAll(Wallet... wallets) {
        return setAll(Collections.EMPTY_LIST);
    }

    @Override
    public boolean setAll(Collection<? extends Wallet> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Wallet... wallets) {
        return removeAll(Arrays.asList(wallets));
    }

    @Override
    public boolean retainAll(Wallet... wallets) {
        return retainAll(Arrays.asList(wallets));
    }

    @Override
    public void remove(int from, int to) {
        removeRange(from, to);
    }

    @Override
    public Wallet get(int index) {
        return store.get(index);
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addListener(ListChangeListener<? super Wallet> listener) {
        throw new UnsupportedOperationException("Listening for changes not supported");
    }

    @Override
    public void removeListener(ListChangeListener<? super Wallet> listener) {
        throw new UnsupportedOperationException("Listening for changes not supported");
    }


}
