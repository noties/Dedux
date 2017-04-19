package ru.noties.dedux.sample;

import android.app.Application;

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

        this.store = AppStore.create(this);
    }

    public Store store() {
        return store;
    }
}
