package dedux.internal;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Consumer;
import dedux.MutableOp;
import dedux.MutableState;
import dedux.PreloadedState;
import dedux.StateItem;
import dedux.Subscription;

@SuppressWarnings("WeakerAccess")
public class MutableStateImpl implements MutableState {

    // todo, we need a way to `persist` whole state, so we do not store it in memory only
    
    private final Map<Class<? extends StateItem>, MutableOp<StateItem>> properties = new HashMap<>();
    private final MutableOp<MutableState> op = new MutableOpImpl<>((MutableState) this);
    private final Consumer<Object> notification = new Consumer<Object>() {
        @Override
        public void apply(@Nonnull Subscription subscription, @Nonnull Object o) {
            notifyOp();
        }
    };


    public MutableStateImpl(@Nullable PreloadedState preloadedState) {
        if (preloadedState != null) {
            state(preloadedState.build());
        }
    }

    @Nonnull
    @Override
    public <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl) {
        synchronized (properties) {
            MutableOp<StateItem> op = properties.get(cl);
            if (op == null) {
                final StateItem initial = ReflectUtils.newInstance(cl);
                op = new MutableOpImpl<>(initial);
                //noinspection unchecked
                ((MutableOp) op).subscribe(notification);
                properties.put(cl, op);
            }
            //noinspection unchecked
            return (MutableOp<S>) op;
        }
    }

    @Override
    public <S extends StateItem> void set(@Nonnull S s) {
        //noinspection unchecked
        final MutableOp<S> op = (MutableOp<S>) get(s.getClass());
        op.set(s);
    }

    @Nonnull
    @Override
    public Map<Class<? extends StateItem>, StateItem> state() {
        final Map<Class<? extends StateItem>, StateItem> map;
        synchronized (properties) {
            map = new HashMap<>(properties.size());
            for (Map.Entry<Class<? extends StateItem>, MutableOp<StateItem>> entry: properties.entrySet()) {
                map.put(entry.getKey(), entry.getValue().get());
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private void state(@Nonnull Map<Class<? extends StateItem>, StateItem> map) {
        synchronized (properties) {
            properties.clear();
            Class<? extends StateItem> cl;
            for (Map.Entry<Class<? extends StateItem>, StateItem> entry: map.entrySet()) {
                cl = entry.getKey();
                final MutableOp op = new MutableOpImpl(entry.getValue());
                op.subscribe(notification);
                properties.put(cl, op);
            }
            // we pass `this` but it doesn't really matter what we pass here (one rule: nonnull)
            notification.apply(new SubscriptionNoOp(), this);
        }
    }

    @Nonnull
    @Override
    public Subscription subscribe(@Nonnull Consumer<MutableState> consumer) {
        return op.subscribe(false, consumer);
    }

    private void notifyOp() {
        op.set(this);
    }
}
