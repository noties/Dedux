package dedux.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // todo, we need a way to `persist` whole state, so we do not store it in the memory only
    //
    // so, what can we do?
    // we can `track` all states by having, for example, a `Set<Class<? extends StateItem>>` that will
    // store all attached states, and a standalone `storage` data type (that can save/restore state when asked)
    //
    // we should provide default implementation that will store everything in memory
    // and give means to store/restore state
    //
    // also, we must define a behavior so MutableOp and its subscribers are preserved...maybe, if property has no subscriptions
    // it can be freed, and if property has subscribers, then it must be preserved
    //
    // another thing... how to give ability to `serialize` the full state without putting it all in memory?
    // can we have a lazy iterator maybe? anyway, for example, if we are planning on synchronizing the full
    // state somewhere in network, we might need to obtain the full state...


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
    public List<StateItem> state() {
        final List<StateItem> list;
        synchronized (properties) {
            list = new ArrayList<>(properties.size());
            for (MutableOp<StateItem> op : properties.values()) {
                list.add(op.get());
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void state(@Nonnull List<StateItem> list) {
        synchronized (properties) {
            properties.clear();
            Class<? extends StateItem> cl;
            for (StateItem item : list) {
                cl = item.getClass();
                final MutableOp op = new MutableOpImpl(item);
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
