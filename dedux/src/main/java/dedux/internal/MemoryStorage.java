package dedux.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.MutableState;
import dedux.StateItem;

// stores full state in memory
public class MemoryStorage extends MutableState.Storage {


    public static MemoryStorage create() {
        return create(null);
    }

    public static MemoryStorage create(@Nullable List<? extends StateItem> initial) {
        return new MemoryStorage(initial);
    }


    private final Map<Class<? extends StateItem>, StateItem> map;

    public MemoryStorage(@Nullable List<? extends StateItem> initial) {
        super(initial);
        this.map = Collections.synchronizedMap(new HashMap<Class<? extends StateItem>, StateItem>());
        if (initial != null) {
            for (StateItem item : initial) {
                map.put(item.getClass(), item);
            }
        }
    }

    @Nullable
    @Override
    public <S extends StateItem> S get(@Nonnull Class<S> cl) {
        //noinspection unchecked
        return (S) map.get(cl);
    }

    @Override
    public <S extends StateItem> void set(@Nonnull S s) {
        map.put(s.getClass(), s);
    }
}
