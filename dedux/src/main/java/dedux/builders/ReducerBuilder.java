package dedux.builders;


import java.util.Collection;
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
    public static <A extends Action> ReducerBuilder<A> create(@Nonnull Class<A> type) {
        return new ReducerBuilder<>(type);
    }


    private final Class<A> type;
    private final Map<Integer, Reducer<? extends Action>> reducers;

    @SuppressWarnings("unused")
    public ReducerBuilder(Class<A> type) {
        this.type = type;
        this.reducers = new HashMap<>();
    }

    public <R extends A> ReducerBuilder<A> add(@Nonnull Reducer<R> reducer) {

        final Class<?> cl = reducer.actionType();

        validateClass(cl);

        // check if not present already
        final int hash = cl.hashCode();

        //noinspection unchecked
        if (reducers.put(hash, reducer) != null) {
            throw new IllegalStateException("Reducer `" + cl.getName() + "` is already registered");
        }

        return this;
    }

    public ReducerBuilder<A> addAll(@Nonnull Collection<Reducer<? extends A>> collection) {
        for (Reducer<? extends A> reducer: collection) {
            if (reducer == null) {
                throw new NullPointerException("Cannot add null reducer");
            }
            add(reducer);
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
            defaultReducer = new ReducerThrows<>(type);
        } else {
            defaultReducer = def;
        }

        return new CompositeReducer<>(type, new HashMap<>(reducers), defaultReducer);
    }

    private static void validateClass(@Nonnull Class<?> cl) {
        // we do not allow interfaces (due to the fact that one class can implement multiple interfaces)
        if (cl.isInterface()) {
            throw new IllegalStateException("Cannot register an interface: " + cl.getName());
        }
    }

    private static class CompositeReducer<A extends Action> implements Reducer<A> {

        private final Class<A> type;
        private final Map<Integer, Reducer<? extends Action>> reducers;
        private final Reducer<A> def;

        CompositeReducer(
                @Nonnull Class<A> type,
                @Nonnull Map<Integer, Reducer<? extends Action>> reducers,
                @Nonnull Reducer<A> def
        ) {
            this.type = type;
            this.reducers = Collections.unmodifiableMap(reducers);
            this.def = def;
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
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
                    // if nothing is found check recursively for a super class
                    reducer = findReducerRecursive(cl.getSuperclass());
                }
                //noinspection unchecked
                out = (Reducer<A>) reducer;
            }

            return out;
        }
    }

    private static class ReducerThrows<A extends Action> implements Reducer<A> {

        private final Class<A> type;

        ReducerThrows(Class<A> type) {
            this.type = type;
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull A action) {
            throw new IllegalStateException(String.format(
                    "Class: `%s`, action: `%s` has no registered reducer", action.getClass().getName(), action
            ));
        }
    }
}
