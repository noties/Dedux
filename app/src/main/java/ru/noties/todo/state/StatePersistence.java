package ru.noties.todo.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.PreloadedState;
import dedux.StateItem;
import dedux.Store;
import dedux.Subscription;
import ru.noties.debug.Debug;
import ru.noties.todo.utils.CollectionUtils;

public class StatePersistence {

    private static final String PREFERENCES_NAME = "todo";
    private static final String KEY = "json";

    private final SharedPreferences preferences;
    private final List<? extends StateItem> initial;
    private final StateSerializer stateSerializer;
    private final Handler handler = new Handler();
    private final Executor executor;

    private Subscription subscription;

    public StatePersistence(
            @Nonnull Context context,
            @Nonnull StateSerializer stateSerializer,
            @Nullable List<? extends StateItem> initial
    ) {
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        this.initial = initial;
        this.stateSerializer = stateSerializer;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public PreloadedState preloadedState() {

        final PreloadedState preloadedState = new PreloadedState();

        // okay, we anyway put our initial items
        if (!CollectionUtils.isEmpty(initial)) {
            for (StateItem item: initial) {
                preloadedState.add(item);
            }
        }

        final Map<Class<? extends StateItem>, StateItem> map = persisted();
        for (StateItem item: map.values()) {
            preloadedState.add(item);
        }

        return preloadedState;
    }

    public void onStoreCreated(@Nonnull Store store) {

        if (subscription != null) {
            throw new IllegalStateException("This persistence is already attached to a store");
        }

        subscription = store.subscribe(($1, state) -> {
            handler.removeCallbacksAndMessages(null);
            // it's kind of debounce (with more or less reasonable timeout)
            handler.postDelayed(() -> {
                final Map<Class<? extends StateItem>, StateItem> map = state.state();
                executor.execute(() -> persist(map));
            }, 1000L);
        });
    }

    private void persist(Map<Class<? extends StateItem>, StateItem> state) {
        Debug.i(state);
        final String serialized = stateSerializer.toJson(state);
        preferences.edit().putString(KEY, serialized).apply();
    }

    @Nonnull
    private Map<Class<? extends StateItem>, StateItem> persisted() {
        final Map<Class<? extends StateItem>, StateItem> map;
        final String serialized = preferences.getString(KEY, null);
        if (TextUtils.isEmpty(serialized)) {
            //noinspection unchecked
            map = Collections.EMPTY_MAP;
        } else {
            map = stateSerializer.fromJson(serialized);
        }
        return map;
    }
}
