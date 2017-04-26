package ru.noties.todo.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.PreloadedState;
import dedux.StateItem;
import dedux.Store;
import dedux.Subscription;
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
            preloadedState.addAll(initial);
        }

        preloadedState.addAll(persisted());

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
                final List<StateItem> list = state.state();
                executor.execute(() -> persist(list));
            }, 1000L);
        });
    }

    private void persist(List<StateItem> list) {
        final String serialized = stateSerializer.toJson(list);
        preferences.edit().putString(KEY, serialized).apply();
    }

    @Nonnull
    private List<StateItem> persisted() {
        final List<StateItem> list;
        final String serialized = preferences.getString(KEY, null);
        if (TextUtils.isEmpty(serialized)) {
            //noinspection unchecked
            list = Collections.EMPTY_LIST;
        } else {
            list = stateSerializer.fromJson(serialized);
        }
        return list;
    }
}
