package dedux;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.internal.SubscriptionNoOp;

@SuppressWarnings("WeakerAccess")
public class CompositeSubscription implements Subscription {

    private final List<Subscription> subscriptions = new ArrayList<>(3);
    private final Object lock = new Object();
    private boolean isUnsubscribed;

    public <T> Consumer<T> compose(@Nonnull final Consumer<T> consumer) {

        synchronized (lock) {

            if (isUnsubscribed) {
                throw new IllegalStateException("Subscription is already unsubscribed: " + this);
            }

            return new Consumer<T>() {
                @Override
                public void apply(@Nonnull Subscription subscription, @Nonnull T t) {

                    synchronized (lock) {
                        if (!isUnsubscribed) {
                            final Subscription noOp = new SubscriptionNoOp();
                            consumer.apply(noOp, t);
                            if (noOp.isUnsubscribed()) {
                                subscription.unsubscribe();
                            } else {
                                subscriptions.add(subscription);
                            }
                        } else {
                            // immediately unsubscribe
                            subscription.unsubscribe();
                        }
                    }

                }
            };
        }
    }

    @Override
    public boolean isUnsubscribed() {
        synchronized (lock) {
            return isUnsubscribed;
        }
    }

    @Override
    public void unsubscribe() {
        synchronized (lock) {
            if (!isUnsubscribed) {
                for (Subscription subscription : subscriptions) {
                    subscription.unsubscribe();
                }
                subscriptions.clear();
                isUnsubscribed = true;
            }
        }
    }

    List<Subscription> subscriptions() {
        return new ArrayList<>(subscriptions);
    }
}
