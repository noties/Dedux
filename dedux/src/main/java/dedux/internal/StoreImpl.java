package dedux.internal;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.MutableState;
import dedux.Op;
import dedux.Reducer;
import dedux.State;
import dedux.StateItem;
import dedux.Store;

public class StoreImpl implements Store {

    private final MutableState state;
    private final Reducer<Action> reducer;
    private final Middleware<Action> middleware;

    // we will create a facade for immutable store, so there is no casts
    // (ensuring immutable behavior where needed)
    private final ImmutableStore immutableStore;
    private final ImmutableState immutableState;

    // we allow only Reducer<Action>, as we should handle all actions
    public StoreImpl(
            @Nonnull MutableState.Storage storage,
            @Nonnull Reducer<Action> reducer,
            @Nullable Middleware<Action> middleware
    ) {
        this.state = new MutableStateImpl(storage);
        this.reducer = reducer;
        this.middleware = middleware == null ? new MiddlewareNoOp() : middleware;

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

        final ActionHandlerImpl<Action> handler = new ActionHandlerImpl<>();
        middleware.apply(immutableStore, action, handler);
        if (!handler.cancelled()) {
            reducer.reduce(state, action);
            for (Middleware.OnActionReduced<Action> onActionReduced : handler.onActionReducedListeners()) {
                onActionReduced.apply(immutableStore, action);
            }
        }
    }

    private static class MiddlewareNoOp implements Middleware<Action> {

        @Nonnull
        @Override
        public Class<Action> actionType() {
            return Action.class;
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull ActionHandler<Action> handler) {
            // no op
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
    }

    private class ImmutableState implements State {

        @Nonnull
        @Override
        public <S extends StateItem> Op<S> get(@Nonnull Class<S> cl) {
            return StoreImpl.this.state.get(cl);
        }
    }

    private static class ActionHandlerImpl<A extends Action> implements Middleware.ActionHandler<A> {

        private final List<Middleware.OnActionReduced<A>> onActionReducedListeners;
        private boolean cancelled;

        ActionHandlerImpl() {
            this.onActionReducedListeners = new ArrayList<>(2);
        }

        @Override
        public void doOnActionReduced(@Nonnull Middleware.OnActionReduced<A> onActionReduced) {
            onActionReducedListeners.add(onActionReduced);
        }

        @Override
        public void cancelActionDispatch() {
            cancelled = true;
            onActionReducedListeners.clear();
        }

        boolean cancelled() {
            return cancelled;
        }

        @Nonnull
        List<Middleware.OnActionReduced<A>> onActionReducedListeners() {
            return onActionReducedListeners;
        }
    }
}
