package dedux;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ReducerBuilder {

    private final Map<Integer, Reducer<? extends Action, StateItem>> reducers;

    public ReducerBuilder() {
        this.reducers = new HashMap<>();
    }

    public <A extends Action, S extends StateItem> ReducerBuilder add(@Nonnull Class<A> cl, @Nonnull Reducer<A, S> reducer) {
        // check if not present already
        final int hash = cl.hashCode();
        //noinspection unchecked
        if (reducers.put(hash, (Reducer<? extends Action, StateItem>) reducer) != null) {
            throw new IllegalStateException("Class `" + cl.getName() + "` is already registered");
        }
        return this;
    }

    public Reducer<Action, StateItem> build() {
        return build(null);
    }

    public Reducer<Action, StateItem> build(@Nullable Reducer<Action, StateItem> def) {

        // if we have nothing, then indicate
        // we check for size & def, because it's perfectly legal to have only one default reducer
        // (not much of a use-case, but still it's legal)
        if (reducers.size() == 0 && def == null) {
            throw new IllegalStateException("No reducers were registered and no default reducer was provided");
        }

        final Reducer<Action, StateItem> defaultReducer;
        if (def == null) {
            defaultReducer = new ReducerThrows();
        } else {
            defaultReducer = def;
        }

        return new CompositeReducer(new HashMap<>(reducers), defaultReducer);
    }

    private static class CompositeReducer implements Reducer<Action, StateItem> {

        private final Map<Integer, Reducer<? extends Action, StateItem>> reducers;
        private final Reducer<Action, StateItem> def;

        CompositeReducer(
                @Nonnull Map<Integer, Reducer<? extends Action, StateItem>> reducers,
                @Nonnull Reducer<Action, StateItem> def
        ) {
            this.reducers = Collections.unmodifiableMap(reducers);
            this.def = def;
        }

        @Nonnull
        @Override
        public StateItem reduce(@Nonnull State state, @Nonnull Action action) {
            final Reducer<Action, StateItem> reducer = findReducer(action.getClass());
            return reducer.reduce(state, action);
        }

        @Nonnull
        private Reducer<Action, StateItem> findReducer(@Nonnull Class<?> cl) {
            final Reducer<Action, StateItem> reducer = findReducerRecursive(cl);
            if (reducer == null) {
                return def;
            } else {
                return reducer;
            }
        }

        @Nullable
        private Reducer<Action, StateItem> findReducerRecursive(@Nullable Class<?> cl) {

            if (cl == null
                    || Object.class == cl) {
                return null;
            }

            final Reducer<Action, StateItem> out;
            {
                // okay, first we check if we have a direct hit
                Reducer<? extends Action, StateItem> reducer = reducers.get(cl.hashCode());
                if (reducer == null) {
                    // if not, check interfaces
                    reducer = findReducerInterface(cl);
                    if (reducer == null) {
                        // if nothing is found find recursively for a super class
                        reducer = findReducerRecursive(cl.getSuperclass());
                    }
                }
                //noinspection unchecked
                out = (Reducer<Action, StateItem>) reducer;
            }

            return out;
        }

        @Nullable
        private Reducer<Action, StateItem> findReducerInterface(@Nonnull Class<?> cl) {

            final Class<?>[] impl = cl.getInterfaces();

            if (impl == null
                    || impl.length == 0) {
                return null;
            }

            Reducer<? extends Action, StateItem> reducer = null;
            for (Class<?> i : impl) {
                reducer = reducers.get(i.hashCode());
                if (reducer != null) {
                    break;
                }
            }

            //noinspection unchecked
            return (Reducer<Action, StateItem>) reducer;
        }
    }

    private static class ReducerThrows implements Reducer<Action, StateItem> {

        @Nonnull
        @Override
        public StateItem reduce(@Nonnull State state, @Nonnull Action action) {
            throw new IllegalStateException(String.format(
                    "Class: `%s`, action: `%s` has no registered reducer", action.getClass().getName(), action
            ));
        }
    }
}
