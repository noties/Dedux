package dedux;

import javax.annotation.Nonnull;

/**
 * An interface that represents immutable application state. Has only one method {@link #get(Class)} and
 * doesn't provide means to mutate the state.
 *
 * @see Op
 * @see StateItem
 * @since 1.0.0
 */
public interface State {

    /**
     * @param cl  type of the state item
     * @param <S> subtype of {@link StateItem}
     * @return an {@link Op}
     * @see StateItem
     * @see Op
     * @since 1.0.0
     */
    @Nonnull
    <S extends StateItem> Op<S> get(@Nonnull Class<S> cl);
}
