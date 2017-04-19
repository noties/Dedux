package ru.noties.dedux.sample.component.core;

import android.view.View;

import javax.annotation.Nonnull;

import dedux.CompositeSubscription;
import dedux.Store;

public abstract class BaseViewComponent {

    protected Store store;
    protected View view;
    protected CompositeSubscription compositeSubscription;

    public void attach(@Nonnull View view, @Nonnull Store store) {
        this.store = store;
        this.view = view;
        this.compositeSubscription = new CompositeSubscription();
        onAttach();
    }

    public void detach() {
        onDetach();
        store = null;
        compositeSubscription.unsubscribe();
    }

    protected abstract void onAttach();

    protected abstract void onDetach();
}
