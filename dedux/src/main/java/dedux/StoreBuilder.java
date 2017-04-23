package dedux;

import javax.annotation.Nonnull;

import dedux.impl.StoreImpl;

@SuppressWarnings("WeakerAccess")
public class StoreBuilder {

    private Middleware<Action> middleware;
    private PreloadedState preloadedState;

    public StoreBuilder() {
    }

    public StoreBuilder middleware(Middleware<Action> middleware) {
        this.middleware = middleware;
        return this;
    }

    public StoreBuilder preloadedState(PreloadedState preloadedState) {
        this.preloadedState = preloadedState;
        return this;
    }

    public Store build(@Nonnull Reducer<Action> reducer) {
        return new StoreImpl(reducer, middleware, preloadedState);
    }
}
