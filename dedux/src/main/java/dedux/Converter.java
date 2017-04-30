package dedux;

import javax.annotation.Nonnull;

/**
 * Interface to be used when converting {@link Op} or {@link MutableOp} to some other object
 *
 * @param <T>
 * @param <R>
 * @since 1.0.0
 */
public interface Converter<T, R> {
    R apply(@Nonnull T t);
}
