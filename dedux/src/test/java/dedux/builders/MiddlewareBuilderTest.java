package dedux.builders;

import org.junit.Test;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.State;
import dedux.Store;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MiddlewareBuilderTest {

    private static class TestAction implements Action {
    }

    @Test
    public void no_middleware_registered_throws() {
        try {
            MiddlewareBuilder.create()
                    .build();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void single_middleware_one_type() {

        final MiddlewareFlag<Action> flag = new MiddlewareFlag<>(Action.class);
        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(flag)
                .build();

        middleware.apply(new StoreNoOp(), createTestAction(), new ActionHandlerNoOp<Action>());
        assertTrue(flag.called);
    }

    @Test
    public void multiple_middleware_one_type() {

        final MiddlewareFlag<Action> first = new MiddlewareFlag<>(Action.class);
        final MiddlewareFlag<Action> second = new MiddlewareFlag<>(Action.class);

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(first)
                .add(second)
                .build();

        middleware.apply(new StoreNoOp(), createTestAction(), new ActionHandlerNoOp<Action>());

        assertTrue(first.called);
        assertTrue(second.called);
    }

    @Test
    public void generic_and_specific_middleware() {

        final MiddlewareFlag<Action> generic = new MiddlewareFlag<>(Action.class);
        final MiddlewareFlag<TestAction> specific = new MiddlewareFlag<>(TestAction.class);

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(generic)
                .add(specific)
                .build();

        // dispatch generic, generic should handle, specific - not

        middleware.apply(new StoreNoOp(), createTestAction(), new ActionHandlerNoOp<Action>());

        assertTrue(generic.called);
        assertFalse(specific.called);

        generic.called = false;
        specific.called = false;

        middleware.apply(new StoreNoOp(), new TestAction(), new ActionHandlerNoOp<Action>());

        assertTrue(generic.called);
        assertTrue(specific.called);
    }

    @Test
    public void cancelled_chain() {

        // second one cancel chain

        final MiddlewareFlag<Action> f = new MiddlewareFlag<>(Action.class);
        final MiddlewareFlag<Action> s = new MiddlewareFlag<Action>(Action.class) {
            @Override
            public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull ActionHandler<Action> handler) {
                super.apply(store, action, handler);
                handler.cancelActionDispatch();
            }
        };
        final MiddlewareFlag<Action> t = new MiddlewareFlag<>(Action.class);

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(f)
                .add(s)
                .add(t)
                .build();

        middleware.apply(new StoreNoOp(), createTestAction(), new ActionHandlerNoOp<Action>());

        assertTrue(f.called);
        assertTrue(s.called);
        assertFalse(t.called);
    }

    @Test
    public void do_on_action_reduced() {

    }

    private static Action createTestAction() {
        return new Action() {
        };
    }

    private static class MiddlewareFlag<A extends Action> implements Middleware<A> {

        private final Class<A> type;
        boolean called;

        MiddlewareFlag(Class<A> type) {
            this.type = type;
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull A action, @Nonnull ActionHandler<A> handler) {
            called = true;
        }
    }

    private static class StoreNoOp implements Store {

        @Nonnull
        @Override
        public State state() {
            //noinspection ConstantConditions
            return null;
        }

        @Override
        public void dispatch(@Nonnull Action action) {

        }
    }

    private static class ActionHandlerNoOp<A extends Action> implements Middleware.ActionHandler<A> {

        @Override
        public void doOnActionReduced(@Nonnull Middleware.OnActionReduced<A> onActionReduced) {

        }

        @Override
        public void cancelActionDispatch() {

        }
    }
}