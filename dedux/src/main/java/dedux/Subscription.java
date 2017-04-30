package dedux;

/**
 * This class is used in {@link Op} to control subscription state
 *
 * @see Op#subscribe(Consumer)
 * @see Op#subscribe(boolean, Consumer)
 * @see CompositeSubscription
 * @since 1.0.0
 */
public interface Subscription {

    /**
     * @return a boolean indicating if this subscription is unsubscribed
     * @since 1.0.0
     */
    boolean isUnsubscribed();

    /**
     * @since 1.0.0
     */
    void unsubscribe();
}
