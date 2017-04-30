package dedux;

import javax.annotation.Nonnull;

/**
 * An interface that represents store of an application. Provides {@link State} to read from (not write)
 * and gives ability to dispatch actions
 *
 * @see State
 * @see Action
 * @since 1.0.0
 */
public interface Store {

    /**
     * @return a {@link State}
     * @see State
     * @since 1.0.0
     */
    @Nonnull
    State state();

    /**
     * @param action to be dispatched
     * @see Action
     * @since 1.0.0
     */
    void dispatch(@Nonnull Action action);
}
