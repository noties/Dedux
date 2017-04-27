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
        final NextFlag nextFlag = new NextFlag();
        middleware.apply(new StoreNoOp(), new Action() {
        }, nextFlag);

        assertTrue(flag.called);
        assertTrue(nextFlag.called);
    }

    @Test
    public void multiple_middleware_one_type() {

        final MiddlewareFlag<Action> first = new MiddlewareFlag<>(Action.class);
        final MiddlewareFlag<Action> second = new MiddlewareFlag<>(Action.class);

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(first)
                .add(second)
                .build();

        final NextFlag nextFlag = new NextFlag();

        middleware.apply(new StoreNoOp(), new Action() {
        }, nextFlag);

        assertTrue(first.called);
        assertTrue(second.called);
        assertTrue(nextFlag.called);
    }

    @Test
    public void generic_and_specific_middleware() {

        final MiddlewareFlag<Action> generic = new MiddlewareFlag<>(Action.class);
        final MiddlewareFlag<TestAction> specific = new MiddlewareFlag<>(TestAction.class);

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(generic)
                .add(specific)
                .build();

        final NextFlag nextFlag = new NextFlag();

        // dispatch generic, generic should handle, specific - not

        middleware.apply(new StoreNoOp(), new Action() {
        }, nextFlag);

        assertTrue(generic.called);
        assertFalse(specific.called);
        assertTrue(nextFlag.called);

        generic.called = false;
        specific.called = false;
        nextFlag.called = false;

        middleware.apply(new StoreNoOp(), new TestAction(), nextFlag);

        assertTrue(generic.called);
        assertTrue(specific.called);
        assertTrue(nextFlag.called);
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
        public void apply(@Nonnull Store store, @Nonnull A action, @Nonnull Next next) {
            called = true;
            next.next();
        }
    }

    private static class NextFlag implements Middleware.Next {

        boolean called;

        @Override
        public void next() {
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
}