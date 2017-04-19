package ru.noties.dedux.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.PreloadedState;
import dedux.Store;
import dedux.Subscription;

class StatePersistance {

    private static final String NAME = "state";
    private static final String KEY = "state";

    private final SharedPreferences preferences;
    private final Handler handler = new Handler();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LoadedState.class, new LoadedStateJsonDeserializer())
            .create();

    private Subscription subscription;

    StatePersistance(@Nonnull Context context) {
        this.preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    // here we just check if we have previous state
    // for now it's a synchronous method
    @Nullable
    PreloadedState preloadedState() {
        return load();
    }

    void onStoreCreated(@Nonnull Store store) {

        if (subscription != null) {
            throw new IllegalStateException("This persistance is already attached to a store");
        }

         subscription = store.subscribe(($1, state) -> {
            handler.removeCallbacksAndMessages(null);
            // it's kind of debounce (with more or less reasonable timeout)
            handler.postDelayed(() -> {
                executor.execute(() -> persist(state.state()));
            }, 1000L);
        });
    }

    private void persist(Map<String, Object> map) {
        final String json = gson.toJson(map);
        preferences.edit().putString(KEY, json).apply();
    }

    private PreloadedState load() {
        final PreloadedState out;
        final String json = preferences.getString(KEY, null);
        if (TextUtils.isEmpty(json)) {
            out = null;
        } else {
            PreloadedState preloadedState = null;
            final LoadedState loadedState = gson.fromJson(json, LoadedState.class);
            if (loadedState != null) {
                final List<Object> objects = loadedState.objects;
                if (objects != null
                        && objects.size() > 0) {
                    preloadedState = new PreloadedState();
                    for (Object object: objects) {
                        preloadedState.add(object);
                    }
                }
            }
            out = preloadedState;
        }
        return out;
    }

    private static class LoadedState {
        final List<Object> objects;
        LoadedState(List<Object> objects) {
            this.objects = objects;
        }
    }

    private static class LoadedStateJsonDeserializer implements JsonDeserializer<LoadedState> {

        @Override
        public LoadedState deserialize(
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {
            final LoadedState state;
            // iterate keys and parse values
            if (jsonElement.isJsonObject()) {
                final List<Object> list = new ArrayList<>();
                state = new LoadedState(list);
                final JsonObject jsonObject = (JsonObject) jsonElement;
                for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
                    final Class<?> cl = parseType(entry.getKey());
                    if (cl != null) {
                        list.add(jsonDeserializationContext.deserialize(entry.getValue(), cl));
                    }
                }
            } else {
                state = null;
            }
            return state;
        }
    }

    private static Class<?> parseType(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
