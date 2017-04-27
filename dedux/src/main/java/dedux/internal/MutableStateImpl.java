package dedux.internal;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import dedux.MutableOp;
import dedux.MutableState;
import dedux.StateItem;

public class MutableStateImpl extends MutableState {

    private final Storage storage;
    private final Map<Class<? extends StateItem>, MutableOp<StateItem>> map;
    private final Object lock;

    public MutableStateImpl(@Nonnull Storage storage) {
        super(storage);
        this.storage = storage;
        this.map = new HashMap<>();
        this.lock = new Object();
    }

    @Nonnull
    @Override
    public <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl) {
        synchronized (lock) {

            MutableOp<StateItem> op = map.get(cl);
            if (op == null) {
                S s = storage.get(cl);
                if (s == null) {
                    s = ReflectUtils.newInstance(cl);
                    storage.set(s);
                }
                op = new MutableOpStateItemImpl<>(storage, (StateItem) s);
                map.put(cl, op);
            }

            //noinspection unchecked
            return (MutableOp<S>) op;
        }
    }

    @Override
    public <S extends StateItem> void set(@Nonnull S s) {
        synchronized (lock) {
            //noinspection unchecked
            final MutableOp<S> op = (MutableOp<S>) get(s.getClass());
            op.set(s);
        }
    }
}
