package dedux.builders;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.MutableState;
import dedux.Reducer;

@SuppressWarnings("WeakerAccess")
public class ReducerBuilder<A extends Action> {


    public static ReducerBuilder<Action> create() {
        return new ReducerBuilder<>(Action.class);
    }

    @SuppressWarnings("unused")
    public static <A extends Action> ReducerBuilder<A> create(@Nonnull Class<A> base) {
        return new ReducerBuilder<>(base);
    }


    private final Map<Integer, Reducer<? extends Action>> reducers;

    @SuppressWarnings("unused")
    public ReducerBuilder(Class<A> base) {
        this.reducers = new HashMap<>();
    }

    public <R extends A> ReducerBuilder<A> add(@Nonnull Class<R> cl, @Nonnull Reducer<? super R> reducer) {
        // check if not present already
        final int hash = cl.hashCode();
        //noinspection unchecked
        if (reducers.put(hash, reducer) != null) {
            throw new IllegalStateException("Class `" + cl.getName() + "` is already registered");
        }
        return this;
    }

    public Reducer<A> build() {
        return build(null);
    }

    public Reducer<A> build(@Nullable Reducer<A> def) {

        // if we have nothing, then indicate
        // we check for size & def, because it's perfectly legal to have only one default reducer
        // (not much of a use-case, but still it's legal)
        if (reducers.size() == 0 && def == null) {
            throw new IllegalStateException("No reducers were registered and no default reducer was provided");
        }

        final Reducer<A> defaultReducer;
        if (def == null) {
            defaultReducer = new ReducerThrows<>();
        } else {
            defaultReducer = def;
        }

        return new CompositeReducer<>(new HashMap<>(reducers), defaultReducer);
    }

    private static class CompositeReducer<A extends Action> implements Reducer<A> {

        private final Map<Integer, Reducer<? extends Action>> reducers;
        private final Reducer<A> def;

        CompositeReducer(
                @Nonnull Map<Integer, Reducer<? extends Action>> reducers,
                @Nonnull Reducer<A> def
        ) {
            this.reducers = Collections.unmodifiableMap(reducers);
            this.def = def;
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A action) {
            final Reducer<A> reducer = findReducer(action.getClass());
            reducer.reduce(state, action);
        }

        @Nonnull
        private Reducer<A> findReducer(@Nonnull Class<?> cl) {
            final Reducer<A> reducer = findReducerRecursive(cl);
            if (reducer == null) {
                return def;
            } else {
                return reducer;
            }
        }

        @Nullable
        private Reducer<A> findReducerRecursive(@Nullable Class<?> cl) {

            if (cl == null
                    || Object.class == cl) {
                return null;
            }

            final Reducer<A> out;
            {
                // okay, first we check if we have a direct hit
                Reducer<? extends Action> reducer = reducers.get(cl.hashCode());
                if (reducer == null) {
                    // if not, check interfaces
                    reducer = findReducerInterface(cl);
                    if (reducer == null) {
                        // if nothing is found find recursively for a super class
                        reducer = findReducerRecursive(cl.getSuperclass());
                    }
                }
                //noinspection unchecked
                out = (Reducer<A>) reducer;
            }

            return out;
        }

        @Nullable
        private Reducer<Action> findReducerInterface(@Nonnull Class<?> cl) {

            final Class<?>[] impl = cl.getInterfaces();

            if (impl == null
                    || impl.length == 0) {
                return null;
            }

            Reducer<? extends Action> reducer = null;
            for (Class<?> i : impl) {
                reducer = reducers.get(i.hashCode());
                if (reducer != null) {
                    break;
                }
            }

            //noinspection unchecked
            return (Reducer<Action>) reducer;
        }
    }

    private static class ReducerThrows<A extends Action> implements Reducer<A> {

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A action) {
            throw new IllegalStateException(String.format(
                    "Class: `%s`, action: `%s` has no registered reducer", action.getClass().getName(), action
            ));
        }
    }
}
