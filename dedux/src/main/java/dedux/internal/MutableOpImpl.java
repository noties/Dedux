package dedux.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import dedux.Consumer;
import dedux.Converter;
import dedux.MutableOp;
import dedux.Op;
import dedux.Subscription;

@SuppressWarnings("WeakerAccess")
public class MutableOpImpl<T> implements MutableOp<T> {

    private final Map<Subscription, Consumer<T>> map;
    private final Object lock = new Object();
    private T value;

    public MutableOpImpl(@Nonnull T t) {
        this.value = t;
        this.map = new HashMap<>(3, .75F);
    }

    @Override
    @Nonnull
    public T get() {
        synchronized (lock) {
            return value;
        }
    }

    @Override
    public <R> R to(@Nonnull Converter<Op<T>, R> converter) {
        synchronized (lock) {
            return converter.apply(this);
        }
    }

    @Override
    public Subscription subscribe(@Nonnull final Consumer<T> consumer) {
        return subscribe(DEF_DELIVER_FIRST, consumer);
    }

    @Override
    public Subscription subscribe(boolean deliverFirst, @Nonnull Consumer<T> consumer) {
        synchronized (lock) {

            final Subscription subscription = new SubscriptionNoOp();

            if (deliverFirst) {
                consumer.apply(subscription, value);
            }

            final Subscription out;

            if (!subscription.isUnsubscribed()) {
                out = new SubscriptionImpl();
                map.put(out, consumer);
            } else {
                out = subscription;
            }

            return out;
        }
    }

    @Override
    public void set(@Nonnull T t) {
        // the thing is...we most likely need to `skip` notification if value is the same
        synchronized (lock) {
            this.value = t;
        }

        notifySubscribers();
    }

    private void notifySubscribers() {

        // maybe it's me, I don't know, but sometimes ConcurrentModificationException is thrown.
        // despite synchronized block, somehow this method and one of the Subscriptions (in map)
        // `unsubscribe` are called on one thread. It's weird, because we do not call directly
        // unsubscribe here on subscriptions (we use a mock flag object) whilst iterating...
        // I have changed implementation slightly to put unsubscribed subscriptions into a list
        // to remove them after we have finished iteration
        synchronized (lock) {

            final SubscriptionFlag flag = new SubscriptionFlag();

            final T value = this.value;

            List<Subscription> mark = null;

            for (Map.Entry<Subscription, Consumer<T>> entry : map.entrySet()) {
                entry.getValue().apply(flag, value);
                if (flag.unsubscribed) {
                    if (mark == null) {
                        mark = new ArrayList<>(2);
                    }
                    mark.add(entry.getKey());
                    flag.unsubscribed = false;
                }
            }

            if (mark != null) {
                for (Subscription subscription : mark) {
                    subscription.unsubscribe();
                }
            }
        }
    }

    // for testing purposes
    List<Subscription> subscriptions() {
        synchronized (lock) {
            return new ArrayList<>(map.keySet());
        }
    }

    private class SubscriptionImpl implements Subscription {

        volatile boolean unsubscribed;

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

        @Override
        public void unsubscribe() {
            synchronized (lock) {
                map.remove(this);
                unsubscribed = true;
            }
        }
    }

    // needed to mutate the state (reset `unsubscribed` flag)
    private static class SubscriptionFlag implements Subscription {

        boolean unsubscribed;

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

        @Override
        public void unsubscribe() {
            unsubscribed = true;
        }
    }
}
