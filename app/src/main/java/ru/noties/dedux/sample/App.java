package ru.noties.dedux.sample;

import android.app.Application;

import java.util.Date;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.MutableState;
import dedux.Reducer;
import dedux.Store;
import dedux.StoreBuilder;
import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class App extends Application {

    private Store store;

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));

        final StatePersistance persistance = new StatePersistance(getApplicationContext());

        store = new StoreBuilder()
                .preloadedState(persistance.preloadedState())
                .middleware((store, action, next) -> {
                    Debug.i("action: %s, state: %s", action, store.state().state());
                    next.next();
                })
                .build((state, action) -> {

                });

        Debug.i("created a store with initial state: %s", store.state().state());

        persistance.onStoreCreated(store);
    }
}
