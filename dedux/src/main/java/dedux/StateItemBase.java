package dedux;

import javax.annotation.Nonnull;

/**
 * Simple implementation of the {@link StateItem}
 *
 * @see StateItem
 * @since 1.0.0
 */
public class StateItemBase implements StateItem {

    /**
     * Required public empty constructor
     *
     * @since 1.0.0
     */
    public StateItemBase() {

    }

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Apply<? super S> apply) {
        //noinspection unchecked
        return Cloner.clone((S) this, apply);
    }

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Class<S> cl, @Nonnull Apply<? super S> apply) {
        //noinspection unchecked
        return Cloner.clone((S) this, apply);
    }
}
