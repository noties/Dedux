package dedux;

import javax.annotation.Nonnull;

/**
 * An interface that is used in {@link StateItem.Cloner}
 *
 * @param <T>
 * @see StateItem
 * @see StateItem.Cloner
 * @see StateItemBase
 * @since 1.0.0
 */
public interface Apply<T> {
    void apply(@Nonnull T t);
}
