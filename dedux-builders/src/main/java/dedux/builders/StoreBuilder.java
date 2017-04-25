package dedux.builders;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.PreloadedState;
import dedux.Reducer;
import dedux.Store;
import dedux.internal.StoreImpl;

@SuppressWarnings("WeakerAccess")
public class StoreBuilder {


    public static StoreBuilder create() {
        return new StoreBuilder();
    }


    private final ReducerBuilder<Action> reducerBuilder;
    private final MiddlewareBuilder<Action> middlewareBuilder;

    private Reducer<Action> defReducer;
    private PreloadedState preloadedState;

    public StoreBuilder() {
        this.reducerBuilder = ReducerBuilder.create();
        this.middlewareBuilder = MiddlewareBuilder.create();
    }

    public <A extends Action> StoreBuilder addModule(@Nonnull Class<A> cl, @Nonnull StoreModule<A> module) {

        this.reducerBuilder.add(cl, module.reducer());

        final Middleware<A> middleware = module.middleware();
        if (middleware != null) {
            this.middlewareBuilder.add(cl, middleware);
        }

        return this;
    }

    public <A extends Action> StoreBuilder addReducer(@Nonnull Class<A> cl, @Nonnull Reducer<? super A> reducer) {
        this.reducerBuilder.add(cl, reducer);
        return this;
    }

    public <A extends Action> StoreBuilder addMiddleware(@Nonnull Class<A> cl, @Nonnull Middleware<? super A> middleware) {
        this.middlewareBuilder.add(cl, middleware);
        return this;
    }


    public StoreBuilder setDefaultReducer(@Nullable Reducer<Action> defReducer) {
        this.defReducer = defReducer;
        return this;
    }

    public StoreBuilder setPreloadedState(@Nullable PreloadedState preloadedState) {
        this.preloadedState = preloadedState;
        return this;
    }


    public Store build() {
        Middleware<Action> middleware = null;
        try {
            middleware = middlewareBuilder.build();
        } catch (IllegalStateException e) {
            // ignored
        }

        return new StoreImpl(reducerBuilder.build(defReducer), middleware, preloadedState);
    }
}
