package dedux.internal;


import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Consumer;
import dedux.Middleware;
import dedux.MutableOp;
import dedux.MutableState;
import dedux.Op;
import dedux.PreloadedState;
import dedux.Reducer;
import dedux.State;
import dedux.StateItem;
import dedux.Store;
import dedux.Subscription;

public class StoreImpl implements Store {

    private final MutableState state;
    private final Reducer<Action> reducer;
    private final Middleware<Action> middleware;
    private final MutableOp<State> op;

    // we will create a facade for immutable store, so there is no casts
    // (ensuring immutable behavior where needed)
    private final ImmutableStore immutableStore;
    private final ImmutableState immutableState;

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
            public void apply(@Nonnull Subscription subscription, @Nonnull MutableState state) {
                op.set(state);
            }
        });

        this.immutableState = new ImmutableState();
        this.immutableStore = new ImmutableStore();
    }

    @Nonnull
    @Override
    public State state() {
        return immutableState;
    }

    @Override
    public void dispatch(@Nonnull final Action action) {
        middleware.apply(immutableStore, action, new Middleware.Next() {
            @Override
            public void next() {
                reducer.reduce(state, action);
            }
        });
    }

    @Nonnull
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


    private class ImmutableStore implements Store {

        @Nonnull
        @Override
        public State state() {
            return StoreImpl.this.immutableState;
        }

        @Override
        public void dispatch(@Nonnull Action action) {
            StoreImpl.this.dispatch(action);
        }

        @Nonnull
        @Override
        public Subscription subscribe(@Nonnull Consumer<State> consumer) {
            return StoreImpl.this.subscribe(consumer);
        }
    }

    private class ImmutableState implements State {

        @Nonnull
        @Override
        public <S extends StateItem> Op<S> get(@Nonnull Class<S> cl) {
            return StoreImpl.this.state.get(cl);
        }

        @Nonnull
        @Override
        public Map<Class<? extends StateItem>, StateItem> state() {
            return StoreImpl.this.state.state();
        }
    }
}
