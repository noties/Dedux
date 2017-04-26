package ru.noties.todo.state;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.StateItem;
import ru.noties.todo.utils.CollectionUtils;

public class StateSerializer {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LoadedState.class, new LoadedStateJsonDeserializer())
            .create();

    @Nullable
    public String toJson(@Nonnull List<StateItem> list) {
        return gson.toJson(new LoadedState(list));
    }

    @Nonnull
    public List<StateItem> fromJson(@Nullable String json) {
        final List<StateItem> list;
        if (TextUtils.isEmpty(json)) {
            //noinspection unchecked
            list = Collections.EMPTY_LIST;
        } else {
            final LoadedState loadedState = gson.fromJson(json, LoadedState.class);
            if (loadedState == null
                    || CollectionUtils.isEmpty(loadedState.list)) {
                //noinspection unchecked
                list = Collections.EMPTY_LIST;
            } else {
                list = loadedState.list;
            }
        }
        return list;
    }

    private static Class<? extends StateItem> parseType(String type) {
        try {
            final Class<?> cl = Class.forName(type);
            if (StateItem.class.isAssignableFrom(cl)) {
                //noinspection unchecked
                return (Class<? extends StateItem>) cl;
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static class LoadedState {
        final List<StateItem> list;

        LoadedState(List<StateItem> list) {
            this.list = list;
        }
    }

    private static class LoadedStateJsonDeserializer implements JsonDeserializer<LoadedState>, JsonSerializer<LoadedState> {

        @Override
        public LoadedState deserialize(
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {

            final LoadedState state;

            // iterate keys and parse values
            if (jsonElement.isJsonObject()) {

                final List<StateItem> list = new ArrayList<>();
                state = new LoadedState(list);

                final JsonObject jsonObject = (JsonObject) jsonElement;

                StateItem item;

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    final Class<? extends StateItem> cl = parseType(entry.getKey());
                    if (cl != null) {
                        item = jsonDeserializationContext.deserialize(entry.getValue(), cl);
                        if (item != null) {
                            list.add(item);
                        }
                    }
                }
            } else {
                state = null;
            }
            return state;
        }

        @Override
        public JsonElement serialize(LoadedState loadedState, Type type, JsonSerializationContext jsonSerializationContext) {
            final JsonObject object = new JsonObject();
            if (loadedState != null
                    && !CollectionUtils.isEmpty(loadedState.list)) {
                for (StateItem item : loadedState.list) {
                    object.add(item.getClass().getName(), jsonSerializationContext.serialize(item));
                }
            }
            return object;
        }
    }
}
