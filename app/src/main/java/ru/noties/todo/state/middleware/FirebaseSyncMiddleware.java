package ru.noties.todo.state.middleware;

import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Middleware;
import dedux.State;
import dedux.Store;
import dedux.Subscription;
import ru.noties.debug.Debug;
import ru.noties.todo.app.model.AccountAuthStateChangedAction;
import ru.noties.todo.state.StateSerializer;
import ru.noties.todo.state.action.FirebaseSyncAction;

public class FirebaseSyncMiddleware implements Middleware<AccountAuthStateChangedAction> {

    private final StateSerializer stateSerializer;
    private final Handler handler = new Handler();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Set<String> acceptedKeys;

    private FirebaseHelper firebaseHelper;
    private Subscription subscription;

    public FirebaseSyncMiddleware(StateSerializer stateSerializer, @Nonnull Set<String> acceptedKeys) {
        this.stateSerializer = stateSerializer;
        this.acceptedKeys = acceptedKeys;
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull AccountAuthStateChangedAction action, @Nonnull Next next) {

        handler.removeCallbacksAndMessages(null);

        if (action.isAuthernticated()) {

            if (firebaseHelper == null) {
                firebaseHelper = createFirebaseHelper(store);
                if (firebaseHelper != null) {
                    firebaseHelper.subscribe();
                }
            }

            if (subscription == null
                    || subscription.isUnsubscribed()) {

                // listen for local changes
                subscription = store.subscribe(($1, state) -> {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(() -> persist(state), 1000L);
                });
            }

        } else {

            // unsubscribe from local changes
            if (subscription != null
                    && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }

            // unsubscribe from remote changes
            if (firebaseHelper != null) {
                firebaseHelper.unsubscribe();
            }
        }

        // pass to next
        next.next();
    }

    @Nullable
    private FirebaseHelper createFirebaseHelper(Store store) {
        final FirebaseHelper helper;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            helper = null;
        } else {
            helper = new FirebaseHelper(FirebaseKey.createKey(user.getEmail()), s -> onNewValueObtained(store, s));
        }
        return helper;
    }

    private void onNewValueObtained(Store store, String value) {
        executor.execute(() -> {
            final Map<String, Object> map = stateSerializer.fromJson(value);
            store.dispatch(new FirebaseSyncAction(map));
        });
    }

    private void persist(State state) {

        executor.execute(() -> {

            if (firebaseHelper != null) {

                final Map<String, Object> map = state.state();
                final Map<String, Object> out = new HashMap<>();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (acceptedKeys.contains(entry.getKey())) {
                        out.put(entry.getKey(), entry.getValue());
                    }
                }

                final String json = stateSerializer.toJson(out);
                firebaseHelper.persist(json);
            }
        });
    }

    private static class FirebaseHelper {

        interface OnNewValueListener {
            void apply(String value);
        }

        private final DatabaseReference reference;
        private final ValueEventListener valueEventListener;

        private boolean subscribed;

        FirebaseHelper(@Nonnull String key, @Nonnull OnNewValueListener listener) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            this.reference = database.getReference(key);
            this.valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Debug.i("dataSnapshot: %s", dataSnapshot);
                    final String value = dataSnapshot.getValue(String.class);
                    listener.apply(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Debug.e(databaseError.toException(), databaseError);
                }
            };
        }

        void persist(String value) {
            Debug.i("value: %s", value);
            reference.setValue(value);
        }

        void subscribe() {
            if (!subscribed) {
                reference.addValueEventListener(valueEventListener);
                subscribed = true;
            }
        }

        void unsubscribe() {
            if (subscribed) {
                reference.removeEventListener(valueEventListener);
                subscribed = false;
            }
        }
    }

    // If you create your own keys, they must be UTF-8 encoded, can be a maximum of 768 bytes,
    // and cannot contain ., $, #, [, ], /, or ASCII control characters 0-31 or 127
    private static class FirebaseKey {

        static String createKey(@Nonnull String in) {
            final StringBuilder builder = new StringBuilder();
            char c;
            for (int i = 0, length = in.length(); i < length; i++) {
                c = in.charAt(i);
                if (!isSupported(c)) {
                    builder.append('_');
                } else {
                    builder.append(c);
                }
            }
            return builder.toString();
        }

        private static boolean isSupported(char c) {
            return Character.isLetterOrDigit(c);
        }
    }
}
