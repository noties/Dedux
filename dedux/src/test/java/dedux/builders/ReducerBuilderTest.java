package dedux.builders;

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.MutableOp;
import dedux.MutableState;
import dedux.Reducer;
import dedux.StateItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReducerBuilderTest {

    private static class TestAction implements Action {
    }

    private static class TestAction_01 implements Action {
    }

    private static class TestAction_02 implements Action {
    }

    private static class TestAction_03 implements Action {
    }

    private static class TestAction_01_01 extends TestAction_01 {
    }

    @Test
    public void duplicate_reducer_added_throws() {
        try {
            ReducerBuilder.create()
                    .add(TestAction.class, ReducerNoOp.create(TestAction.class))
                    .add(TestAction.class, ReducerNoOp.create(TestAction.class));
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void no_def_no_reducers_added_throws() {
        try {
            ReducerBuilder.create()
                    .build();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void no_def_provided_throws_for_unknown_action() {

        final Reducer<Action> reducer = ReducerBuilder.create()
                .add(TestAction.class, ReducerNoOp.create(TestAction.class))
                .build();

        final Action action = new Action() {
        };

        try {
            reducer.reduce(new MutableStateNoOp(), action);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void reducer_called_simple() {

        final ReducerFlag<TestAction> testActionReducer = new ReducerFlag<>(TestAction.class);
        final Reducer<Action> reducer = ReducerBuilder.create()
                .add(TestAction.class, testActionReducer)
                .build();

        reducer.reduce(new MutableStateNoOp(), new TestAction());
        assertTrue(testActionReducer.called);
    }

    @Test
    public void reducer_called_correctly_3_actions() {

        final ReducerFlag<TestAction_01> action01Reducer = new ReducerFlag<>(TestAction_01.class);
        final ReducerFlag<TestAction_02> action02Reducer = new ReducerFlag<>(TestAction_02.class);
        final ReducerFlag<TestAction_03> action03Reducer = new ReducerFlag<>(TestAction_03.class);

        final Reducer<Action> reducer = ReducerBuilder.create()
                .add(TestAction_01.class, action01Reducer)
                .add(TestAction_02.class, action02Reducer)
                .add(TestAction_03.class, action03Reducer)
                .build();

        @SuppressWarnings("WeakerAccess")
        final class ReduceChecker {

            final ReducerFlag[] flags = {
                    action01Reducer,
                    action02Reducer,
                    action03Reducer
            };

            void check(int calledIndex) {
                for (int i = 0, length = flags.length; i < length; i++) {
                    assertEquals(calledIndex == i, flags[i].called);
                }
            }

            void clear() {
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0, length = flags.length; i < length; i++) {
                    flags[i].called = false;
                }
            }
        }
        final ReduceChecker checker = new ReduceChecker();

        reducer.reduce(new MutableStateNoOp(), new TestAction_01());
        checker.check(0);
        checker.clear();

        reducer.reduce(new MutableStateNoOp(), new TestAction_02());
        checker.check(1);
        checker.clear();

        reducer.reduce(new MutableStateNoOp(), new TestAction_03());
        checker.check(2);
    }

    @Test
    public void sibling_action_correct_reducer() {

        final ReducerFlag<TestAction_01> action01ReducerFlag = new ReducerFlag<>(TestAction_01.class);
        final Reducer<Action> reducer = ReducerBuilder.create()
                .add(TestAction_01.class, action01ReducerFlag)
                .build();

        reducer.reduce(new MutableStateNoOp(), new TestAction_01_01());
        assertTrue(action01ReducerFlag.called);
    }

    @Test
    public void default_reducer_called() {
        final ReducerFlag<Action> reducerFlag = new ReducerFlag<>(Action.class);
        final Reducer<Action> reducer = ReducerBuilder.create()
                .add(TestAction.class, ReducerNoOp.create(TestAction.class))
                .build(reducerFlag);

        reducer.reduce(new MutableStateNoOp(), new Action() {
        });
        assertTrue(reducerFlag.called);
    }

    private static class ReducerNoOp<A extends Action> implements Reducer<A> {

        static <A extends Action> Reducer<A> create(Class<A> type) {
            return new ReducerNoOp<>(type);
        }

        private final Class<A> type;

        ReducerNoOp(Class<A> type) {
            this.type = type;
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A a) {
        }
    }

    private static class ReducerFlag<A extends Action> implements Reducer<A> {

        private final Class<A> type;
        boolean called;

        ReducerFlag(Class<A> type) {
            this.type = type;
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A a) {
            called = true;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class MutableStateNoOp extends MutableState {

        MutableStateNoOp() {
            super(new StorageNoOp());
        }

        @Nonnull
        @Override
        public <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl) {
            return null;
        }

        @Override
        public <S extends StateItem> void set(@Nonnull S s) {

        }

        private static class StorageNoOp extends Storage {

            StorageNoOp() {
                super(null);
            }

            @Nullable
            @Override
            public <S extends StateItem> S get(@Nonnull Class<S> cl) {
                return null;
            }

            @Override
            public <S extends StateItem> void set(@Nonnull S s) {

            }
        }
    }
}