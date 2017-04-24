package dedux;

import org.junit.Test;

import java.util.Map;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReducerBuilderTest {

    private static class TestAction implements Action {}

    private static class TestAction_01 implements Action {}
    private static class TestAction_02 implements Action {}
    private static class TestAction_03 implements Action {}

    private static class TestAction_01_01 extends TestAction_01 {}

    @Test
    public void duplicate_reducer_added_throws() {
        try {
            new ReducerBuilder()
                    .add(TestAction.class, ReducerNoOp.<TestAction>create())
                    .add(TestAction.class, ReducerNoOp.<TestAction>create());
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void no_def_no_reducers_added_throws() {
        try {
            new ReducerBuilder()
                    .build();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void no_def_provided_throws_for_unknown_action() {

        final Reducer<Action> reducer = new ReducerBuilder()
                .add(TestAction.class, ReducerNoOp.<TestAction>create())
                .build();

        final Action action = new Action() {};

        try {
            reducer.reduce(new MutableStateNoOp(), action);
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void reducer_called_simple() {

        final ReducerFlag<TestAction> testActionReducer = new ReducerFlag<>();
        final Reducer<Action> reducer = new ReducerBuilder()
                .add(TestAction.class, testActionReducer)
                .build();

        reducer.reduce(new MutableStateNoOp(), new TestAction());
        assertTrue(testActionReducer.called);
    }

    @Test
    public void reducer_called_correctly_3_actions() {

        final ReducerFlag<TestAction_01> action01Reducer = new ReducerFlag<>();
        final ReducerFlag<TestAction_02> action02Reducer = new ReducerFlag<>();
        final ReducerFlag<TestAction_03> action03Reducer = new ReducerFlag<>();

        final Reducer<Action> reducer = new ReducerBuilder()
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

        final ReducerFlag<TestAction_01> action01ReducerFlag = new ReducerFlag<>();
        final Reducer<Action> reducer = new ReducerBuilder()
                .add(TestAction_01.class, action01ReducerFlag)
                .build();

        reducer.reduce(new MutableStateNoOp(), new TestAction_01_01());
        assertTrue(action01ReducerFlag.called);
    }

    @Test
    public void default_reducer_called() {
        final ReducerFlag<Action> reducerFlag = new ReducerFlag<>();
        final Reducer<Action> reducer = new ReducerBuilder()
                .add(TestAction.class, ReducerNoOp.<TestAction>create())
                .build(reducerFlag);

        reducer.reduce(new MutableStateNoOp(), new Action() {});
        assertTrue(reducerFlag.called);
    }

    private static class ReducerNoOp<A extends Action> implements Reducer<A> {

        static <A extends Action> Reducer<A> create() {
            return new ReducerNoOp<>();
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A a) {
        }
    }

    private static class ReducerFlag<A extends Action> implements Reducer<A> {

        boolean called;

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A a) {
            called = true;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static class MutableStateNoOp implements MutableState {

        @Nonnull
        @Override
        public <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl) {
            return null;
        }

        @Override
        public <S extends StateItem> void set(@Nonnull S s) {

        }

        @Nonnull
        @Override
        public Subscription subscribe(@Nonnull Consumer<MutableState> consumer) {
            return null;
        }

        @Nonnull
        @Override
        public Map<Class<? extends StateItem>, StateItem> state() {
            return null;
        }
    }
}