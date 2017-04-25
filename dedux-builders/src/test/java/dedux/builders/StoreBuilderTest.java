package dedux.builders;

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.MutableState;
import dedux.Reducer;
import dedux.Store;

import static org.junit.Assert.*;

public class StoreBuilderTest {

    private interface Action_01 extends Action {}
    private interface Action_02 extends Action {}

    private static class Action_01_01 implements Action_01 {}
    private static class Action_01_02 implements Action_01 {}

    private static class Action_02_01 implements Action_02 {}
    private static class Action_02_02 implements Action_02 {}

    @Test
    public void empty_builder_throws() {
        try {
            StoreBuilder.create()
                    .build();
            assertTrue(false);
        } catch (IllegalStateException e) {
            // this exception is actually thrown by ReducerBuilder
            assertTrue(true);
        }
    }

    @Test
    public void two_modules_reducers() {

        final ReducerFlag<Action_01_01> flag_01_01 = ReducerFlag.create();
        final ReducerFlag<Action_01_02> flag_01_02 = ReducerFlag.create();
        final ReducerFlag<Action_02_01> flag_02_01 = ReducerFlag.create();
        final ReducerFlag<Action_02_02> flag_02_02 = ReducerFlag.create();

        final ReducerFlag[] flags = new ReducerFlag[] {
                flag_01_01,
                flag_01_02,
                flag_02_01,
                flag_02_02
        };

        final int[][] map = new int[4][1];

        final Store store = StoreBuilder.create()
                .addModule(Action_01.class, new StoreModuleAdapter<Action_01>() {
                    @Nonnull
                    @Override
                    public Reducer<Action_01> reducer() {
                        return ReducerBuilder.create(Action_01.class)
                                .add(Action_01_01.class, flag_01_01)
                                .add(Action_01_02.class, flag_01_02)
                                .build();
                    }
                })
                .addModule(Action_02.class, new StoreModuleAdapter<Action_02>() {
                    @Nonnull
                    @Override
                    public Reducer<Action_02> reducer() {
                        return ReducerBuilder.create(Action_02.class)
                                .add(Action_02_01.class, flag_02_01)
                                .add(Action_02_02.class, flag_02_02)
                                .build();
                    }
                })
                .build();

        //noinspection WeakerAccess
        final class FlagChecker {
            void check() {
                for (int i = 0, length = map.length; i < length; i++) {
                    assertEquals(map[i][0], flags[i].count);
                }
            }
        }
        final FlagChecker checker = new FlagChecker();

        // initial check, all 0
        checker.check();

        store.dispatch(new Action_01_01());
        map[0][0] = 1;
        checker.check();

        store.dispatch(new Action_01_02());
        map[1][0] = 1;
        checker.check();

        store.dispatch(new Action_02_01());
        map[2][0] = 1;
        checker.check();

        store.dispatch(new Action_02_02());
        map[3][0] = 1;
        checker.check();
    }

    private static class ReducerFlag<A extends Action> implements Reducer<A> {

        static <A extends Action> ReducerFlag<A> create() {
            return new ReducerFlag<>();
        }

        int count;

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A a) {
            count += 1;
        }
    }

    private abstract static class StoreModuleAdapter<A extends Action> implements StoreModule<A> {
        @Nonnull
        @Override
        public abstract Reducer<A> reducer();

        @Nullable
        @Override
        public Middleware<A> middleware() {
            return null;
        }
    }
}