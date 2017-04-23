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

    @Nullable
    public String toJson(@Nullable List<Object> objects) {

        final String out;
        if (CollectionUtils.isEmpty(objects)) {
            out = null;
        } else {

            final Map<String, Object> map = new HashMap<>(objects.size());
            for (Object o: objects) {
                map.put(o.getClass().getName(), o);
            }

            out = gson.toJson(map);
        }
        return out;
    }

    @Nonnull
    public List<Object> fromJson(@Nullable String json) {
        final List<Object> out;
        if (TextUtils.isEmpty(json)) {
            //noinspection unchecked
            out = Collections.EMPTY_LIST;
        } else {
            final LoadedState loadedState = gson.fromJson(json, LoadedState.class);
            if (loadedState == null
                    || CollectionUtils.isEmpty(loadedState.objects)) {
                //noinspection unchecked
                out = Collections.EMPTY_LIST;
            } else {
                out = loadedState.objects;
            }
        }
        return out;
    }

    private static Class<?> parseType(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            return null;
        }
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
}
