package dedux.builders;

import org.junit.Test;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Consumer;
import dedux.Middleware;
import dedux.State;
import dedux.Store;
import dedux.Subscription;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MiddlewareBuilderTest {

    private static class TestAction implements Action {}

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

        final MiddlewareFlag<Action> flag = new MiddlewareFlag<>();
        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(Action.class, flag)
                .build();
        final NextFlag nextFlag = new NextFlag();
        middleware.apply(new StoreNoOp(), new Action() {}, nextFlag);

        assertTrue(flag.called);
        assertTrue(nextFlag.called);
    }

    @Test
    public void multiple_middleware_one_type() {

        final MiddlewareFlag<Action> first = new MiddlewareFlag<>();
        final MiddlewareFlag<Action> second = new MiddlewareFlag<>();

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(Action.class, first)
                .add(Action.class, second)
                .build();

        final NextFlag nextFlag = new NextFlag();

        middleware.apply(new StoreNoOp(), new Action() {}, nextFlag);

        assertTrue(first.called);
        assertTrue(second.called);
        assertTrue(nextFlag.called);
    }

    @Test
    public void generic_and_specific_middleware() {

        final MiddlewareFlag<Action> generic = new MiddlewareFlag<>();
        final MiddlewareFlag<TestAction> specific = new MiddlewareFlag<>();

        final Middleware<Action> middleware = MiddlewareBuilder.create()
                .add(Action.class, generic)
                .add(TestAction.class, specific)
                .build();

        final NextFlag nextFlag = new NextFlag();

        // dispatch generic, generic should handle, specific - not

        middleware.apply(new StoreNoOp(), new Action() {}, nextFlag);

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

        boolean called;

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

        @Nonnull
        @Override
        public Subscription subscribe(@Nonnull Consumer<State> consumer) {
            //noinspection ConstantConditions
            return null;
        }
    }
}