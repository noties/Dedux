package dedux.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Consumer;
import dedux.Converter;
import dedux.MutableOp;
import dedux.Op;
import dedux.Subscription;

@SuppressWarnings("WeakerAccess")
public class MutableOpImpl<T> implements MutableOp<T> {

    private static final boolean DELIVER_FIRST = false;

    private final Map<Subscription, Consumer<T>> map;
    private final Object lock = new Object();
    private T value;

    public MutableOpImpl(@Nullable T t) {
        this.value = t;
        this.map = new HashMap<>(3, .75F);
    }

    @Override
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
        return subscribe(DELIVER_FIRST, consumer);
    }

    @Override
    public Subscription subscribe(boolean deliverFirst, @Nonnull Consumer<T> consumer) {
        synchronized (lock) {

            final Subscription subscription = new SubscriptionImpl();
            map.put(subscription, consumer);

            if (deliverFirst) {
                consumer.apply(subscription, value);
            }

            return subscription;
        }
    }

    @Override
    public void set(T t) {
        synchronized (lock) {
            this.value = t;
        }
        notifySubscribers();
    }

    private void notifySubscribers() {
        final SubscriptionFlag flag = new SubscriptionFlag();
        synchronized (lock) {
            final T value = this.value;
            final Iterator<Map.Entry<Subscription, Consumer<T>>> iterator = map.entrySet().iterator();
            Map.Entry<Subscription, Consumer<T>> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                entry.getValue().apply(flag, value);
                if (flag.unsubscribed) {
                    iterator.remove();
                    flag.unsubscribed = false;
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
