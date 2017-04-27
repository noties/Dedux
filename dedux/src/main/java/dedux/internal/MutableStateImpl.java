package dedux.internal;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import dedux.MutableOp;
import dedux.MutableState;
import dedux.StateItem;

public class MutableStateImpl extends MutableState {

    private final Storage storage;
    private final Map<Class<? extends StateItem>, SoftReference<MutableOp<StateItem>>> map;
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

            SoftReference<MutableOp<StateItem>> reference = map.get(cl);

            MutableOp<StateItem> op = reference != null
                    ? reference.get()
                    : null;

            if (op == null) {
                // it's really doesn't matter why (reference was released or we never actually retrieved value)
                S s = storage.get(cl);
                if (s == null) {
                    // storage can return NULL indicating that this value is not yet present
                    s = ReflectUtils.newInstance(cl);
                    storage.set(s);
                }

                op = new MutableOpStateItemImpl<>(storage, (StateItem) s);
                reference = new SoftReference<MutableOp<StateItem>>(op);

                map.put(cl, reference);
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
