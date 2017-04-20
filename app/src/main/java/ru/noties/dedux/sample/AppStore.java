package ru.noties.dedux.sample;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.MiddlewareBuilder;
import dedux.Reducer;
import dedux.ReducerBuilder;
import dedux.Store;
import dedux.StoreBuilder;
import ru.noties.debug.Debug;
import ru.noties.dedux.sample.app.components.appbar.AppBarState;
import ru.noties.dedux.sample.app.components.input.InputAction;
import ru.noties.dedux.sample.app.components.input.InputReducer;
import ru.noties.dedux.sample.app.components.input.InputState;
import ru.noties.dedux.sample.state.StatePersistence;

class AppStore {

    static Store create(@Nonnull Application application) {
        return new AppStore(application).create();
    }

    private final Application application;

    private AppStore(@Nonnull Application application) {
        this.application = application;
    }

    private Store create() {

        final StatePersistence persistence = new StatePersistence(
                application.getApplicationContext(),
                initialState()
        );

        final Store store = new StoreBuilder()
                .preloadedState(persistence.preloadedState())
                .middleware(middleware())
                .build(reducer());

        persistence.onStoreCreated(store);

        return store;
    }

    private List<Object> initialState() {
        final List<Object> list = new ArrayList<>();
        list.add(new AppBarState(application.getString(R.string.app_name), false, AppBarState.ActionState.INVISIBLE));
        list.add(new InputState());
        return list;
    }

    private Reducer<Action> reducer() {
        return new ReducerBuilder()
                .add(InputAction.class, new InputReducer())
                .build((s, a) -> {});
    }

    private Middleware<Action> middleware() {
        return new MiddlewareBuilder()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s, currentState: %s", action, store.state().state());
                    next.next();
                }))
                .build();
    }
}
