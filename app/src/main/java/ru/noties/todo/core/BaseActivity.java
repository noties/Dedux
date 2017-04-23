package ru.noties.todo.core;

import android.app.Activity;
import android.os.Bundle;

import dedux.CompositeSubscription;
import dedux.Store;
import ru.noties.todo.StoreHolder;

public abstract class BaseActivity extends Activity {

    private Store store;
    private CompositeSubscription compositeSubscription;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        store = ((StoreHolder) getApplicationContext()).store();
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
        compositeSubscription = null;
    }

    public Store store() {
        return store;
    }

    public CompositeSubscription compositeSubscription() {
        return compositeSubscription;
    }
}
