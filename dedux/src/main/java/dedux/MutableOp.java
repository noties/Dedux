package dedux;

import javax.annotation.Nonnull;

/**
 * An extension over {@link Op} that have a method to update underlying value, which
 * triggers notification to all subscriptions
 *
 * @param <T>
 * @see Op
 * @since 1.0.0
 */
public interface MutableOp<T> extends Op<T> {

    /**
     * A method that updates underlying value and triggers notification for all subscriptions
     *
     * @param t new value to be set
     * @see Op#get()
     * @see Op#subscribe(Consumer)
     * @see Op#subscribe(boolean, Consumer)
     */
    void set(@Nonnull T t);
}
