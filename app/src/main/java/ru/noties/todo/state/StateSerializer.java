package ru.noties.todo.state;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.noties.todo.utils.CollectionUtils;

public class StateSerializer {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LoadedState.class, new LoadedStateJsonDeserializer())
            .create();

    // we need to accept Map<String, Object> because
    // no value (aka NULL) is a perfectly valid value, but we cannot ask for a class of a null
    // so we need to introduce a standalone key

    @Nullable
    public String toJson(@Nonnull Map<String, Object> map) {
        return gson.toJson(map);
    }

    @Nonnull
    public Map<String, Object> fromJson(@Nullable String json) {
        final Map<String, Object> map;
        if (TextUtils.isEmpty(json)) {
            //noinspection unchecked
            map = Collections.EMPTY_MAP;
        } else {
            final LoadedState loadedState = gson.fromJson(json, LoadedState.class);
            if (loadedState == null
                    || CollectionUtils.isEmpty(loadedState.map)) {
                //noinspection unchecked
                map = Collections.EMPTY_MAP;
            } else {
                map = loadedState.map;
            }
        }
        return map;
    }

    private static Class<?> parseType(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static class LoadedState {
        final Map<String, Object> map;
        LoadedState(Map<String, Object> map) {
            this.map = map;
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
                final Map<String, Object> map = new HashMap<>();
                state = new LoadedState(map);
                final JsonObject jsonObject = (JsonObject) jsonElement;
                for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
                    final Class<?> cl = parseType(entry.getKey());
                    if (cl != null) {
                        map.put(cl.getName(), jsonDeserializationContext.deserialize(entry.getValue(), cl));
                    }
                }
            } else {
                state = null;
            }
            return state;
        }
    }
}
