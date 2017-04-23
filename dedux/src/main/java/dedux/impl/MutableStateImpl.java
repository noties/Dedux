package dedux.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Consumer;
import dedux.MutableOp;
import dedux.MutableState;
import dedux.PreloadedState;
import dedux.Subscription;

public class MutableStateImpl implements MutableState {

    private final Map<Class<?>, MutableOp<?>> properties = new HashMap<>();
    private final MutableOp<MutableState> op = new MutableOpImpl<>((MutableState) this);
    private final Consumer<Object> notification = new Consumer<Object>() {
        @Override
        public void apply(@Nonnull Subscription subscription, @Nullable Object o) {
            notifyOp();
        }
    };

    public MutableStateImpl(@Nullable PreloadedState preloadedState) {
        if (preloadedState != null) {
            state(preloadedState.build());
        }
    }

    @Override
    public <R> MutableOp<R> get(@Nonnull Class<R> cl) {
        synchronized (properties) {
            MutableOp<?> op = properties.get(cl);
            if (op == null) {
                op = new MutableOpImpl<R>(null);
                //noinspection unchecked
                ((MutableOp) op).subscribe(notification);
                properties.put(cl, op);
            }
            //noinspection unchecked
            return (MutableOp<R>) op;
        }
    }

    @Override
    public <T> void set(@Nonnull T t) {
        //noinspection unchecked
        get((Class<T>) t.getClass()).set(t);
    }

    @Nonnull
    @Override
    public Map<String, Object> state() {
        // key is the Class<?> name
        final Map<String, Object> map;
        synchronized (properties) {
            map = new HashMap<>(properties.size());
            for (Map.Entry<Class<?>, MutableOp<?>> entry: properties.entrySet()) {
                map.put(entry.getKey().getName(), entry.getValue().get());
            }
        }
        return map;
    }


    private void state(@Nonnull Map<String, Object> map) {
        synchronized (properties) {
            properties.clear();
            Class<?> cl;
            for (Map.Entry<String, Object> entry: map.entrySet()) {
                cl = stateClass(entry.getKey());
                if (cl == null) {
                    throw new IllegalStateException("Cannot create a class: `" + entry.getKey() + "`");
                }
                //noinspection unchecked
                final MutableOp op = new MutableOpImpl(entry.getValue());
                //noinspection unchecked
                op.subscribe(notification);
                properties.put(cl, op);
            }

            notification.apply(new SubscriptionNoOp(), null);
        }
    }

    @Override
    public Subscription subscribe(@Nonnull Consumer<MutableState> consumer) {
        return op.subscribe(false, consumer);
    }

    private void notifyOp() {
        op.set(this);
    }

    private static Class<?> stateClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
