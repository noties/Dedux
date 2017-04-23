package dedux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class MiddlewareBuilder {

    private final Map<Class<? extends Action>, List<Middleware<Action>>> middlewares;

    public MiddlewareBuilder() {
        this.middlewares = new LinkedHashMap<>();
    }

    // order of registered middlewares is important, so earlier registered -> earlier in a chain

    // here we can have duplicates of types, so having 2 middlewares of the same type just
    // will place them in one chain call
    public <A extends Action> MiddlewareBuilder add(@Nonnull Class<A> cl, @Nonnull Middleware<A> middleware) {
        List<Middleware<Action>> current = middlewares.get(cl);
        if (current == null) {
            current = new ArrayList<>(3);
            //noinspection unchecked
            current.add((Middleware<Action>) middleware);
            middlewares.put(cl, current);
        } else {
            //noinspection unchecked
            current.add((Middleware<Action>) middleware);
        }
        return this;
    }

    public Middleware<Action> build() {
        if (middlewares.size() == 0) {
            throw new IllegalStateException("No middlewares were registered");
        }
        return new CompositeMiddleware(new LinkedHashMap<>(middlewares));
    }

    private static class CompositeMiddleware implements Middleware<Action> {

        private final Map<Class<? extends Action>, List<Middleware<Action>>> middlewares;

        private CompositeMiddleware(Map<Class<? extends Action>, List<Middleware<Action>>> middlewares) {
            this.middlewares = Collections.unmodifiableMap(middlewares);
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull Next next) {
            final List<Middleware<Action>> list = findMiddlewaresForClass(action.getClass());
            if (list == null
                    || list.size() == 0) {
                // nothing is found, just pass further
                next.next();
            } else {
                final MiddlewareChain chain = new MiddlewareChain(list);
                chain.apply(store, action, next);
            }
        }

        private List<Middleware<Action>> findMiddlewaresForClass(Class<? extends Action> cl) {
            final List<Middleware<Action>> out = new ArrayList<>();
            for (Class<? extends Action> registered : middlewares.keySet()) {
                if (registered.isAssignableFrom(cl)) {
                    out.addAll(middlewares.get(registered));
                }
            }
            return out;
        }

        private static class MiddlewareChain implements Middleware<Action> {

            private final Iterator<Middleware<Action>> iterator;

            MiddlewareChain(@Nonnull List<Middleware<Action>> list) {
                this.iterator = list.iterator();
            }

            @Override
            public void apply(@Nonnull final Store store, @Nonnull final Action action, @Nonnull final Next next) {
                if (iterator.hasNext()) {
                    final Middleware<Action> middleware = iterator.next();
                    middleware.apply(store, action, new Next() {
                        @Override
                        public void next() {
                            iterator.remove();
                            MiddlewareChain.this.apply(store, action, next);
                        }
                    });
                } else {
                    next.next();
                }
            }
        }
    }
}
