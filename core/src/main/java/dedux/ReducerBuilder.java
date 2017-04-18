package dedux;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ReducerBuilder {

    private final Map<Integer, Reducer<? extends Action>> reducers;

    public ReducerBuilder() {
        this.reducers = new HashMap<>();
    }

    public <A extends Action> ReducerBuilder add(@Nonnull Class<A> cl, @Nonnull Reducer<A> reducer) {
        // check if not present already
        final int hash = cl.hashCode();
        if (reducers.put(hash, reducer) != null) {
            throw new IllegalStateException("Class `" + cl.getName() + "` is already registered");
        }
        return this;
    }

    public Reducer<Action> build() {
        return build(null);
    }

    public Reducer<Action> build(@Nullable Reducer<Action> def) {

        // if we have nothing, then indicate
        // we check for size & def, because it's perfectly legal to have only one default reducer
        // (not much of a use-case, but still it's legal)
        if (reducers.size() == 0 && def == null) {
            throw new IllegalStateException("No reducers were registered and no default reducer was provided");
        }

        final Reducer<Action> defaultReducer;
        if (def == null) {
            defaultReducer = new ReducerThrows();
        } else {
            defaultReducer = def;
        }

        return new CompositeReducer(new HashMap<>(reducers), defaultReducer);
    }

    private static class CompositeReducer implements Reducer<Action> {

        private final Map<Integer, Reducer<? extends Action>> reducers;
        private final Reducer<Action> def;

        CompositeReducer(Map<Integer, Reducer<? extends Action>> reducers, Reducer<Action> def) {
            this.reducers = Collections.unmodifiableMap(reducers);
            this.def = def;
        }

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull Action action) {
            final Reducer<Action> reducer = findReducer(action.getClass());
            reducer.reduce(state, action);
        }

        private Reducer<Action> findReducer(Class<?> cl) {
            final Reducer<Action> reducer = findReducerRecursive(cl);
            if (reducer == null) {
                return def;
            } else {
                return reducer;
            }
        }

        private Reducer<Action> findReducerRecursive(Class<?> cl) {

            if (cl == null
                    || Object.class == cl) {
                return null;
            }

            final Reducer<Action> out;
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
                out = (Reducer<Action>) reducer;
            }

            return out;
        }

        private Reducer<Action> findReducerInterface(Class<?> cl) {

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

    private static class ReducerThrows implements Reducer<Action> {

        @Override
        public void reduce(@Nonnull MutableState state, @Nonnull Action action) {
            throw new IllegalStateException(String.format(
                    "Class: `%s`, action: `%s` has no registered reducer", action.getClass().getName(), action
            ));
        }
    }
}
