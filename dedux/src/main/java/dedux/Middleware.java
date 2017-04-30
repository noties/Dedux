package dedux;

import java.util.concurrent.CancellationException;

import javax.annotation.Nonnull;

/**
 * Unlike {@link Reducer} can have async logic and, with the help of {@link dedux.builders.MiddlewareBuilder}
 * can handle multiple actions
 *
 * @param <A>
 * @see dedux.builders.MiddlewareBuilder
 * @since 1.0.0
 */
public interface Middleware<A extends Action> {

    /**
     * This is a convenience method to be used primary in {@link dedux.builders.MiddlewareBuilder},
     * so there is no need to call {@code builder.add(SomeClass.class, Middleware<SomeClass>) },
     * plus it gives ability to register a {@link java.util.Collection} of middlewares (and
     * possibility to obtain collections of middleware from different modules).
     * Over than in {@link dedux.builders.MiddlewareBuilder} there is no currently another usage
     * of this method
     *
     * @return Class of `A` (not many options here)
     * @see dedux.builders.MiddlewareBuilder
     * @since 1.0.0
     */
    @Nonnull
    Class<A> actionType();

    /**
     * @param store  a {@link Store} instance
     * @param action dispatched {@link Action}
     * @throws CancellationException if further processing of an action should halt
     * @since 1.0.0
     */
    void apply(@Nonnull Store store, @Nonnull A action) throws CancellationException;
}
