package dedux;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;

import dedux.impl.SubscriptionNoOp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CompositeSubscriptionTest {

    private CompositeSubscription subscription;

    @Before
    public void before() {
        subscription = new CompositeSubscription();
    }

    @After
    public void after() {
        subscription.unsubscribe();
    }

    @Test
    public void returns_not_null() {
        assertNotNull(subscription.compose(new Consumer<Object>() {
            @Override
            public void apply(@Nonnull Subscription subscription, Object o) {

            }
        }));
    }

    @Test
    public void not_unsubscribed() {
        assertFalse(subscription.isUnsubscribed());
    }

    @Test
    public void sync_unsubscribe() {
        assertFalse(subscription.isUnsubscribed());
        subscription.unsubscribe();
        assertTrue(subscription.isUnsubscribed());
    }

    @Test
    public void unsubscribed_throws() {
        assertFalse(subscription.isUnsubscribed());
        subscription.unsubscribe();
        assertTrue(subscription.isUnsubscribed());

        try {
            subscription.compose(new Consumer<java.lang.Object>() {
                @Override
                public void apply(@Nonnull Subscription subscription, Object o) {

                }
            });
            //noinspection ConstantConditions
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void subscription_unsubscribed_not_added() {
        subscription.compose(new Consumer<Object>() {
            @Override
            public void apply(@Nonnull Subscription subscription, Object o) {
                subscription.unsubscribe();
            }
        });

        assertEquals(0, subscription.subscriptions().size());
    }

    @Test
    public void subscription_not_added_before_applied() {
        subscription.compose(new Consumer<Object>() {
            @Override
            public void apply(@Nonnull Subscription subscription, Object o) {

            }
        });

        assertEquals(0, subscription.subscriptions().size());
    }

    @Test
    public void subscription_added() {
        final Consumer<Object> action = subscription.compose(new Consumer<Object>() {
            @Override
            public void apply(@Nonnull Subscription subscription, Object o) {

            }
        });
        final SubscriptionNoOp noOp = new SubscriptionNoOp();
        action.apply(noOp, null);
        assertEquals(1, subscription.subscriptions().size());
    }

    @Test
    public void subscription_unsubscribed_applied_action() {
        final Consumer<Object> action = subscription.compose(new Consumer<Object>() {
            @Override
            public void apply(@Nonnull Subscription subscription, Object o) {
                // first we unsubscribe our main composite subscription
                subscription.unsubscribe();
            }
        });
        action.apply(new SubscriptionNoOp(), null);

        assertEquals(0, subscription.subscriptions().size());
    }

}