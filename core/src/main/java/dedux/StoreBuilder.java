package dedux;

import javax.annotation.Nonnull;

import dedux.impl.StoreImpl;

@SuppressWarnings("WeakerAccess")
public class StoreBuilder {

    public static StoreBuilder create(@Nonnull Reducer<Action> reducer) {
        return new StoreBuilder(reducer);
    }

    private final Reducer<Action> reducer;
    private Middleware<Action> middleware;
    private PreloadedState preloadedState;

    public StoreBuilder(@Nonnull Reducer<Action> reducer) {
        this.reducer = reducer;
    }

    public StoreBuilder middleware(Middleware<Action> middleware) {
        this.middleware = middleware;
        return this;
    }

    public StoreBuilder preloadedState(PreloadedState preloadedState) {
        this.preloadedState = preloadedState;
        return this;
    }

    public Store build() {
        return new StoreImpl(reducer, middleware, preloadedState);
    }
}
