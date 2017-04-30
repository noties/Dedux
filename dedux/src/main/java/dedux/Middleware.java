package dedux;

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
     * Simple listener interface to be used with {@link ActionHandler}
     *
     * @param <A>
     * @see ActionHandler
     * @see Store
     * @see Action
     * @since 1.0.0
     */
    interface OnActionReduced<A extends Action> {
        void apply(@Nonnull Store store, @Nonnull A action);
    }

    /**
     * Handler for processing actions. Does two things: gives ability to be notified when action
     * is finally reduced and cancel action dispatch.
     *
     * @param <A>
     * @see OnActionReduced
     * @since 1.0.0
     */
    interface ActionHandler<A extends Action> {

        /**
         * Subscribe for action finally reduced event. If some other middleware canceled action dispatch
         * via {@link #cancelActionDispatch()} then this method won't be called
         *
         * @param onActionReduced listener to be called
         * @see OnActionReduced
         * @since 1.0.0
         */
        void doOnActionReduced(@Nonnull OnActionReduced<A> onActionReduced);

        /**
         * Cancel further dispatch of this action
         *
         * @since 1.0.0
         */
        void cancelActionDispatch();
    }

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
     * @param store   a {@link Store}
     * @param action  dispatched {@link Action}
     * @param handler a {@link ActionHandler} instance for additional actions
     * @see Store
     * @see Action
     * @see ActionHandler
     * @since 1.0.0
     */
    void apply(@Nonnull Store store, @Nonnull A action, @Nonnull ActionHandler<A> handler);
}
