package dedux;

import javax.annotation.Nonnull;

/**
 * Interface that is used when subscribing for {@link Op} updates.
 *
 * @param <T>
 * @see Op
 * @see Op#subscribe(Consumer)
 * @since 1.0.0
 */
public interface Consumer<T> {

    /**
     * @param subscription a {@link Subscription} object that can be used to unsubscribe
     *                     inside `apply` method call
     * @param t            value that triggered notification {@link MutableOp#set(Object)}
     * @see Subscription
     * @see Op
     * @since 1.0.0
     */
    void apply(@Nonnull Subscription subscription, @Nonnull T t);
}
