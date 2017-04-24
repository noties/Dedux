package dedux.internal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Consumer;
import dedux.Converter;
import dedux.MutableOp;
import dedux.Op;
import dedux.Subscription;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MutableOpImplTest {

    @Test
    public void initial_value_present() {
        final MutableOp<String> op = new MutableOpImpl<>("yo");
        assertEquals("yo", op.get());
    }

    @Test
    public void set_called_get_returns() {
        final MutableOp<String> op = new MutableOpImpl<>("initial");
        assertEquals("initial", op.get());
        op.set("yo");
        assertEquals("yo", op.get());
    }

    @Test
    public void converter_simple() {
        final MutableOp<String> op = new MutableOpImpl<>("123");
        final Integer integer = op.to(new Converter<Op<String>, Integer>() {
            @Override
            public Integer apply(@Nonnull Op<String> stringOp) {
                return Integer.parseInt(stringOp.get());
            }
        });
        assertEquals(123, integer.intValue());
    }

    @Test
    public void deliver_first_default_value() {
        // default value for deliver first is FALSE
        final MutableOp<String> op = new MutableOpImpl<>("yo");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        op.subscribe(consumerHistory);
        assertEquals(0, consumerHistory.list.size());
    }

    @Test
    public void deliver_first_false() {
        final MutableOp<String> op = new MutableOpImpl<>("yo");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        op.subscribe(false, consumerHistory);
        assertEquals(0, consumerHistory.list.size());
    }

    @Test
    public void deliver_first_true() {
        final MutableOp<String> op = new MutableOpImpl<>("yo");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        op.subscribe(true, consumerHistory);
        assertEquals(1, consumerHistory.list.size());
        assertEquals("yo", consumerHistory.list.get(0));
    }

    @Test
    public void deliver_first_false_subsequent_delivered() {
        final MutableOp<String> op = new MutableOpImpl<>("yo");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        op.subscribe(false, consumerHistory);
        assertEquals(0, consumerHistory.list.size());
        op.set("yo2");
        op.set("yo3");
        assertEquals(2, consumerHistory.list.size());
        assertEquals("yo2", consumerHistory.list.get(0));
        assertEquals("yo3",  consumerHistory.list.get(1));
    }

    @Test
    public void deliver_first_true_all_delivered() {
        final MutableOp<String> op = new MutableOpImpl<>("yo1");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        op.subscribe(true, consumerHistory);
        assertEquals(1, consumerHistory.list.size());
        op.set("yo2");
        op.set("yo3");
        assertEquals(3, consumerHistory.list.size());
        assertArrayEquals(new String[] { "yo1", "yo2", "yo3" }, consumerHistory.list.toArray(new String[3]));
    }

    @Test
    public void subscription_unsubscribed_on_value() {
        final MutableOpImpl<String> op = new MutableOpImpl<>("yo");
        // so after we receive first value we unsubscribe and everything should be ok
        final ConsumerFlag<String> consumerFlag = new ConsumerFlag<String>() {
            @Override
            public void apply(@Nonnull Subscription subscription, @Nullable String s) {
                super.apply(subscription, s);
                subscription.unsubscribe();
            }
        };
        op.subscribe(true, consumerFlag);
        assertTrue(consumerFlag.called);
        assertEquals(0, op.subscriptions().size());
    }

    @Test
    public void subscription_manual_unsubscribe() {
        final MutableOpImpl<String> op = new MutableOpImpl<>("yo");
        final ConsumerHistory<String> consumerHistory = new ConsumerHistory<>();
        final Subscription subscription = op.subscribe(false, consumerHistory);
        op.set("yo2");
        op.set("yo3");
        assertEquals(2, consumerHistory.list.size());
        assertFalse(subscription.isUnsubscribed());
        assertEquals(1, op.subscriptions().size());

        subscription.unsubscribe();
        assertTrue(subscription.isUnsubscribed());
        assertEquals(0, op.subscriptions().size());
    }

    private static class ConsumerHistory<T> implements Consumer<T> {

        final List<T> list = new ArrayList<>();

        @Override
        public void apply(@Nonnull Subscription subscription, @Nullable T t) {
            list.add(t);
        }
    }

    private static class ConsumerFlag<T> implements Consumer<T> {

        boolean called;

        @Override
        public void apply(@Nonnull Subscription subscription, @Nullable T t) {
            called = true;
        }
    }
}