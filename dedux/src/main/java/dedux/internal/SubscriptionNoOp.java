package dedux.internal;

import javax.annotation.concurrent.NotThreadSafe;

import dedux.Subscription;

@NotThreadSafe
public class SubscriptionNoOp implements Subscription {

    private boolean unsubscribed;

    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    @Override
    public void unsubscribe() {
        unsubscribed = true;
    }
}
