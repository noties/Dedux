package dedux;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple {@link dedux.MutableState.Storage} implementation that stores its values
 * in memory only
 *
 * @since 1.0.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MemoryStorage extends MutableState.Storage {


    /**
     * Factory method to build a new empty instance of {@link MemoryStorage}
     *
     * @return new instance of {@link MemoryStorage} without initial data (empty)
     * @see #create(List)
     * @since 1.0.0
     */
    public static MemoryStorage create() {
        return create(null);
    }

    /**
     * Factory method to build a new instance of {@link MemoryStorage}
     *
     * @param initial nullable list of initial items that this storage has
     * @return new instance of {@link MemoryStorage}
     * @since 1.0.0
     */
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
