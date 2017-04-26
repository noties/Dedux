package ru.noties.todo.app;

import android.content.Context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.CompositeSubscription;
import dedux.StateItemBase;
import dedux.Store;
import dedux.androidcomponent.StoreHolder;

public class ComponentHelper {

    public interface OnNewStateListener<T extends StateItemBase> {
        void onNewState(@Nonnull T t);
    }

    @Nullable
    public static ComponentHelper install(@Nonnull Context context) {

        final ComponentHelper out;
        final StoreHolder holder;

        final Context appContext = context.getApplicationContext();

        if (context instanceof StoreHolder) {
            holder = (StoreHolder) context;
        } else if (appContext instanceof StoreHolder) {
            holder = (StoreHolder) appContext;
        } else {
            holder = null;
        }

        if (holder == null) {
            out = null;
        } else {
            out = new ComponentHelper(holder.store());
        }

        return out;
    }

    private final Store store;
    private CompositeSubscription subscription;

    ComponentHelper(@Nonnull Store store) {
        this.store = store;
    }

    public <T extends StateItemBase> void attach(@Nonnull Class<T> stateType, @Nonnull OnNewStateListener<T> listener) {

        if (subscription == null
                || subscription.isUnsubscribed()) {
            subscription = new CompositeSubscription();
        }

        store.state()
                .get(stateType)
                .subscribe(true, subscription.compose(($1, s) -> listener.onNewState(s)));
    }

    public void detach() {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = null;
        }
    }

    public Store store() {
        return store;
    }
}
