package dedux.sample.todo.store;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.MutableState;
import dedux.StateItem;

public class AppStorage extends MutableState.Storage {

    private final SharedPreferences preferences;
    private final Gson gson;
    private final Map<Class<? extends StateItem>, StateItem> memoryCache;

    public AppStorage(
            @Nonnull SharedPreferences preferences,
            @Nullable List<? extends StateItem> initial) {
        super(initial);
        this.preferences = preferences;
        this.gson = new Gson();
        this.memoryCache = Collections.synchronizedMap(new LRUMap<>(33));

        // okay, here is the thing about initial state here
        // we will not update current item if it's already saved
        // but different application can have different requirements, so this part
        // should be carefully thought through (providing some kind of expected behaviour, override, skip, etc)
        if (initial != null) {
            for (StateItem item : initial) {
                if (item != null) {
                    if (!preferences.contains(item.getClass().getName())) {
                        set(item);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public <S extends StateItem> S get(@Nonnull Class<S> cl) {
        synchronized (memoryCache) {
            StateItem item = memoryCache.get(cl);
            if (item == null) {
                item = readFromPrefs(cl);
                if (item != null) {
                    memoryCache.put(cl, item);
                }
            }
            //noinspection unchecked
            return (S) item;
        }
    }

    @Override
    public <S extends StateItem> void set(@Nonnull S s) {
        synchronized (memoryCache) {
            memoryCache.put(s.getClass(), s);
            writeToPrefs(s);
        }
    }

    @Nullable
    private <S extends StateItem> S readFromPrefs(@Nonnull Class<S> cl) {
        final S s;
        final String value = preferences.getString(cl.getName(), null);
        if (TextUtils.isEmpty(value)) {
            s = null;
        } else {
            s = gson.fromJson(value, cl);
        }
        return s;
    }

    private <S extends StateItem> void writeToPrefs(@Nonnull S s) {
        final String value = gson.toJson(s);
        preferences.edit().putString(s.getClass().getName(), value).apply();
    }

    private static class LRUMap<K, V> extends LinkedHashMap<K, V> {

        private final int max;

        LRUMap(int max) {
            super(max + 1, .75F, true);
            this.max = max;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > max;
        }
    }
}
