package ru.noties.tddd.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Consumer;
import dedux.MutableState;
import dedux.PreloadedState;
import dedux.State;
import dedux.Store;
import dedux.Subscription;
import ru.noties.debug.Debug;
import ru.noties.tddd.utils.CollectionUtils;

public class StatePersistence {

    private static final String NAME = "state";
    private static final String KEY = "state";

    private final SharedPreferences preferences;
    private final Handler handler = new Handler();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LoadedState.class, new LoadedStateJsonDeserializer())
            .create();

    private final List<Object> initial;

    private Subscription subscription;

    public StatePersistence(@Nonnull Context context, @Nullable List<Object> initial) {
        this.preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        this.initial = initial;
    }

    // here we just check if we have previous state
    // for now it's a synchronous method
    @Nullable
    public PreloadedState preloadedState() {
        return load();
    }

    public void onStoreCreated(@Nonnull Store store) {

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

//        firebaseDebug(store);
    }

    private void persist(Map<String, Object> map) {
        final String json = gson.toJson(map);
        preferences.edit().putString(KEY, json).apply();
    }

    private PreloadedState load() {
        final PreloadedState out;
        final String json = preferences.getString(KEY, null);
        if (TextUtils.isEmpty(json)) {
            if (!CollectionUtils.isEmpty(initial)) {
                out = new PreloadedState();
                for (Object o: initial) {
                    out.add(o);
                }
            } else {
                out = null;
            }
        } else {
            PreloadedState preloadedState = null;
            final LoadedState loadedState = gson.fromJson(json, LoadedState.class);
            if (loadedState != null) {
                final List<Object> objects = loadedState.objects;
                final Map<Class<?>, Object> mapInitial = mapInitial();
                preloadedState = new PreloadedState();
                if (objects != null
                        && objects.size() > 0) {
                    for (Object object: objects) {
                        preloadedState.add(object);
                        mapInitial.remove(object.getClass());
                    }
                }
                if (mapInitial.size() > 0) {
                    for (Object o: mapInitial.values()) {
                        preloadedState.add(o);
                    }
                }
            }
            out = preloadedState;
        }
        return out;
    }

    private Map<Class<?>, Object> mapInitial() {
        final Map<Class<?>, Object> out;
        if (initial != null
                && initial.size() > 0) {
            out = new HashMap<>();
            for (Object o: initial) {
                out.put(o.getClass(), o);
            }
        } else {
            //noinspection unchecked
            out = Collections.EMPTY_MAP;
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

    private void firebaseDebug(Store store) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        Debug.i(user);
        if (user == null) {
            auth.signInWithEmailAndPassword("debug@debug.debug", "Debug11")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                launchFirebaseUser(store);
                            } else {
                                Debug.e(task.getException());
                            }
                        }
                    });
        } else {
            launchFirebaseUser(store);
        }
    }

    private void launchFirebaseUser(Store store) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("debug");
        final Handler handler = new Handler();
        final Holder holder = new Holder(null);
        store.subscribe(new Consumer<State>() {
            @Override
            public void apply(@Nonnull Subscription subscription, @Nullable State state) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    final String json = gson.toJson(state.state());
                    holder.send = json;
                    reference.setValue(json);
                }, 2500L);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String s = dataSnapshot.getValue(String.class);
                if (TextUtils.equals(s, holder.send)) {
                    return;
                }

                final LoadedState loadedState = gson.fromJson(s, LoadedState.class);
                final MutableState state = (MutableState) store.state();

                for (Object o: loadedState.objects) {
                    state.set(o);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static class Holder {
        String send;

        public Holder(String send) {
            this.send = send;
        }
    }
}
