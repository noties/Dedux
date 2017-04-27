package dedux.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public static <A extends Action> MiddlewareBuilder<A> create(Class<A> base) {
        return new MiddlewareBuilder<>(base);
    }


    private final Map<Class<? extends A>, List<Middleware<A>>> middlewares;

    @SuppressWarnings("unused")
    public MiddlewareBuilder(Class<A> base) {
        this.middlewares = new LinkedHashMap<>();
    }

    // order of registered middlewares is important, so earlier registered -> earlier in a chain

    // here we can have duplicates of types, so having 2 middlewares of the same type just
    // will place them in one chain call
    public <R extends A> MiddlewareBuilder<A> add(@Nonnull Class<R> cl, @Nonnull Middleware<? super R> middleware) {
        List<Middleware<A>> current = middlewares.get(cl);
        if (current == null) {
            current = new ArrayList<>(3);
            //noinspection unchecked
            current.add((Middleware<A>) middleware);
            middlewares.put(cl, current);
        } else {
            //noinspection unchecked
            current.add((Middleware<A>) middleware);
        }
        return this;
    }

    public Middleware<A> build() {
        if (middlewares.size() == 0) {
            throw new IllegalStateException("No middlewares were registered");
        }
        return new CompositeMiddleware<>(new LinkedHashMap<>(middlewares));
    }


    private static class CompositeMiddleware<A extends Action> implements Middleware<A> {

        private final Map<Class<? extends A>, List<Middleware<A>>> middlewares;

        private CompositeMiddleware(Map<Class<? extends A>, List<Middleware<A>>> middlewares) {
            this.middlewares = Collections.unmodifiableMap(middlewares);
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull A action, @Nonnull Next next) {
            //noinspection unchecked
            final List<Middleware<A>> list = findMiddlewaresForClass((Class<? extends A>) action.getClass());
            if (list == null
                    || list.size() == 0) {
                // nothing is found, just pass further
                next.next();
            } else {
                final MiddlewareChain<A> chain = new MiddlewareChain<>(list);
                chain.apply(store, action, next);
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

            private final Iterator<Middleware<A>> iterator;

            MiddlewareChain(@Nonnull List<Middleware<A>> list) {
                this.iterator = list.iterator();
            }

            @Override
            public void apply(@Nonnull final Store store, @Nonnull final A action, @Nonnull final Next next) {
                if (iterator.hasNext()) {
                    final Middleware<A> middleware = iterator.next();
                    middleware.apply(store, action, new Next() {
                        @Override
                        public void next() {
                            iterator.remove();
                            apply(store, action, next);
                        }
                    });
                } else {
                    next.next();
                }
            }
        }
    }
}
