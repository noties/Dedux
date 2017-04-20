package ru.noties.dedux.sample;

import android.app.Application;

import dedux.Store;
import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class App extends Application implements StoreHolder {

    private Store store;

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));

        this.store = AppStore.create(this);
    }

    @Override
    public Store store() {
        return store;
    }
}