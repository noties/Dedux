package dedux;

import javax.annotation.Nonnull;

/**
 * A class that represents _observable property_. Doesn't have means of mutating underlying value
 *
 * @param <T>
 * @see MutableOp
 * @since 1.0.0
 */
public interface Op<T> {

    /**
     * Default value for {@link #subscribe(Consumer)} call
     *
     * @see #subscribe(boolean, Consumer)
     * @since 1.0.0
     */
    boolean DEF_DELIVER_FIRST = false;

    /**
     * Synchronous method to obtain a value associated with this property
     *
     * @return a property of type `T`
     * @since 1.0.0
     */
    @Nonnull
    T get();

    /**
     * Subscribe for changes of underlying value. Represents a stream of updates valid until
     * {@link Subscription#unsubscribe()} is called
     *
     * @param consumer a {@link Consumer}
     * @return a {@link Subscription}
     * @see #subscribe(boolean, Consumer)
     * @see Subscription
     * @see Consumer
     * @since 1.0.0
     */
    Subscription subscribe(@Nonnull Consumer<T> consumer);

    /**
     * @param deliverFirst flag indicating if initial value should be delivered on subscription.
     *                     `true` will deliver current item, `false` won't deliver initial value,
     *                     only a value that is updated after subscription is initialized
     * @param consumer     a {@link Consumer}
     * @return a {@link Subscription}
     * @see #DEF_DELIVER_FIRST
     * @see Subscription
     * @see Consumer
     * @since 1.0.0
     */
    Subscription subscribe(boolean deliverFirst, @Nonnull Consumer<T> consumer);

    /**
     * Convenience method to convert this property to another thing. For example
     * to another implementation of _observable_ pattern
     * {@code
     * final Observable<String> observable = op.to(in -> Observable.just(in.get()))
     * }
     *
     * @param converter to convert this instance
     * @param <R>       the return type (result of {@link Converter#apply(Object)} operation
     * @return a result from the {@link Converter#apply(Object)} operation
     * @see Converter
     * @since 1.0.0
     */
    <R> R to(@Nonnull Converter<Op<T>, R> converter);
}
