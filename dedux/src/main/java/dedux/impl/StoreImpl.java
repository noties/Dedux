package dedux.impl;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Consumer;
import dedux.Middleware;
import dedux.MutableOp;
import dedux.MutableState;
import dedux.PreloadedState;
import dedux.Reducer;
import dedux.State;
import dedux.Store;
import dedux.Subscription;

public class StoreImpl implements Store {

    private final MutableState state;
    private final Reducer<Action> reducer;
    private final Middleware<Action> middleware;
    private final MutableOp<State> op;

    // we allow only Reducer<Action>, as we should handle all actions
    public StoreImpl(
            @Nonnull Reducer<Action> reducer,
            @Nullable Middleware<Action> middleware,
            @Nullable PreloadedState preloadedState
    ) {
        this.state = new MutableStateImpl(preloadedState);
        //noinspection unchecked
        this.reducer = reducer;
        this.middleware = middleware == null ? new MiddlewareNoOp() : middleware;
        this.op = new MutableOpImpl<>((State) state);

        this.state.subscribe(new Consumer<MutableState>() {
            @Override
            public void apply(@Nonnull Subscription subscription, @Nullable MutableState state) {
                op.set(state);
            }
        });
    }

    @Nonnull
    @Override
    public State state() {
        return state;
    }

    @Override
    public void dispatch(@Nonnull final Action action) {
        middleware.apply(this, action, new Middleware.Next() {
            @Override
            public void next() {
                reducer.reduce(state, action);
            }
        });
    }

    @Override
    public Subscription subscribe(@Nonnull Consumer<State> consumer) {
        return op.subscribe(consumer);
    }

    private static class MiddlewareNoOp implements Middleware<Action> {

        @Override
        public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull Next next) {
            next.next();
        }
    }
}
