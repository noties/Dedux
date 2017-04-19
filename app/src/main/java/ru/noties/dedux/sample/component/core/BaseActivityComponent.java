package ru.noties.dedux.sample.component.core;

import android.app.Activity;

import javax.annotation.Nonnull;

import dedux.CompositeSubscription;
import dedux.Store;

public abstract class BaseActivityComponent {

    protected Store store;
    protected Activity activity;
    protected CompositeSubscription compositeSubscription;

    public void attach(@Nonnull Activity activity, @Nonnull Store store) {
        this.store = store;
        this.activity = activity;
        this.compositeSubscription = new CompositeSubscription();
        onAttach();
    }

    public void detach() {
        onDetach();
        compositeSubscription.unsubscribe();
        compositeSubscription = null;
        store = null;
        activity = null;
    }

    protected abstract void onAttach();
    protected abstract void onDetach();
}
