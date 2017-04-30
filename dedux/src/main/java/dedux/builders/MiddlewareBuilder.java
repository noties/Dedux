package dedux.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.Store;

@SuppressWarnings("WeakerAccess")
public class MiddlewareBuilder<A extends Action> {


    public static MiddlewareBuilder<Action> create() {
        return new MiddlewareBuilder<>(Action.class);
    }

    @SuppressWarnings("unused")
    public static <A extends Action> MiddlewareBuilder<A> create(Class<A> type) {
        return new MiddlewareBuilder<>(type);
    }

    private final Class<A> type;
    private final Map<Class<? extends A>, List<Middleware<A>>> middlewares;

    @SuppressWarnings("unused")
    public MiddlewareBuilder(Class<A> type) {
        this.type = type;
        this.middlewares = new LinkedHashMap<>();
    }

    // order of registered middlewares is important, so earlier registered -> earlier in a chain

    // here we can have duplicates of types, so having 2 middlewares of the same type just
    // will place them in one chain call
    public MiddlewareBuilder<A> add(@Nonnull Middleware<? extends A> middleware) {

        final Class<? extends A> cl = middleware.actionType();

        List<Middleware<A>> current = middlewares.get(cl);
        if (current == null) {
            current = new ArrayList<>(2);
            //noinspection unchecked
            current.add((Middleware<A>) middleware);
            middlewares.put(cl, current);
        } else {
            //noinspection unchecked
            current.add((Middleware<A>) middleware);
        }
        return this;
    }

    // as order of the added middlewares is important, we allow a list (not a collection) only
    public MiddlewareBuilder<A> addAll(@Nonnull List<Middleware<? extends A>> list) {
        for (Middleware<? extends A> middleware : list) {
            if (middleware == null) {
                throw new NullPointerException("Cannot register null value");
            }
            add(middleware);
        }
        return this;
    }

    public Middleware<A> build() {
        if (middlewares.size() == 0) {
            throw new IllegalStateException("No middlewares were registered");
        }
        return new CompositeMiddleware<>(type, new LinkedHashMap<>(middlewares));
    }


    private static class CompositeMiddleware<A extends Action> implements Middleware<A> {

        private final Class<A> type;
        private final Map<Class<? extends A>, List<Middleware<A>>> middlewares;

        private CompositeMiddleware(Class<A> type, Map<Class<? extends A>, List<Middleware<A>>> middlewares) {
            this.type = type;
            this.middlewares = Collections.unmodifiableMap(middlewares);
        }

        @Nonnull
        @Override
        public Class<A> actionType() {
            return type;
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull A action) throws CancellationException {
            //noinspection unchecked
            final List<Middleware<A>> list = findMiddlewaresForClass((Class<? extends A>) action.getClass());
            if (list != null
                    && list.size() > 0) {
                final MiddlewareChain<A> chain = new MiddlewareChain<>(type, list);
                chain.apply(store, action);
            }
        }

        private List<Middleware<A>> findMiddlewaresForClass(Class<? extends A> cl) {
            final List<Middleware<A>> out = new ArrayList<>();
            for (Class<? extends A> registered : middlewares.keySet()) {
                if (registered.isAssignableFrom(cl)) {
                    out.addAll(middlewares.get(registered));
                }
            }
            return out;
        }

        private static class MiddlewareChain<A extends Action> implements Middleware<A> {

            private final Class<A> type;
            private final List<Middleware<A>> list;

            MiddlewareChain(@Nonnull Class<A> type, @Nonnull List<Middleware<A>> list) {
                this.type = type;
                this.list = list;
            }

            @Nonnull
            @Override
            public Class<A> actionType() {
                return type;
            }

            @Override
            public void apply(@Nonnull final Store store, @Nonnull final A action) throws CancellationException {
                for (Middleware<A> middleware : list) {
                    middleware.apply(store, action);
                }
            }
        }
    }
}
