package dedux;

import javax.annotation.Nonnull;

/**
 * A synchronous reducer for specific {@link Action}. Important difference from `redux` is that
 * `reduce` method is void. Reducer is the only component of `dedux` that can actually write
 * to the state ({@link MutableState}).
 *
 * @param <A>
 * @see dedux.builders.ReducerBuilder
 * @see Action
 * @since 1.0.0
 */
public interface Reducer<A extends Action> {

    /**
     * This method primary usage is in {@link dedux.builders.ReducerBuilder}
     *
     * @return the type of the {@link Action} that this reducer `reduces`
     * @see dedux.builders.ReducerBuilder
     * @since 1.0.0
     */
    @Nonnull
    Class<A> actionType();

    /**
     * @param state a {@link MutableState} to read from and write to
     * @param a     a {@link Action} that was dispatched
     * @see MutableState
     * @see Action
     * @since 1.0.0
     */
    void reduce(@Nonnull MutableState state, @Nonnull A a);
}
