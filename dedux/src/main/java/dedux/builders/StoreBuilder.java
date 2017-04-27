package dedux.builders;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.MutableState;
import dedux.Reducer;
import dedux.Store;
import dedux.internal.StoreImpl;

public class StoreBuilder {

    public static StoreBuilder create() {
        return new StoreBuilder();
    }

    private MutableState.Storage storage;
    private Reducer<Action> reducer;
    private Middleware<Action> middleware;

    public StoreBuilder() {
    }

    public StoreBuilder storage(@Nonnull MutableState.Storage storage) {
        this.storage = storage;
        return this;
    }

    public StoreBuilder reducer(@Nonnull Reducer<Action> reducer) {
        this.reducer = reducer;
        return this;
    }

    public StoreBuilder middleware(@Nullable Middleware<Action> middleware) {
        this.middleware = middleware;
        return this;
    }

    public Store build() {
        if (reducer == null) {
            throw new NullPointerException("Reducer must not be null");
        }
        if (storage == null) {
            throw new NullPointerException("MutableState.Storage must not be null");
        }
        return new StoreImpl(storage, reducer, middleware);
    }

}
