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
import ru.noties.dedux.sample.app.model.AddTodoAction;
import ru.noties.dedux.sample.app.model.AddTodoReducer;
import ru.noties.dedux.sample.app.model.CheckDoneAction;
import ru.noties.dedux.sample.app.model.CheckDoneReducer;
import ru.noties.dedux.sample.app.model.ClearDoneAction;
import ru.noties.dedux.sample.app.model.ClearDoneReducer;
import ru.noties.dedux.sample.app.model.ModifyTodoAction;
import ru.noties.dedux.sample.app.model.ModifyTodoMiddleware;
import ru.noties.dedux.sample.app.model.TodosState;
import ru.noties.dedux.sample.app.model.ToggleAllDoneAction;
import ru.noties.dedux.sample.app.model.ToggleAllDoneReducer;
import ru.noties.dedux.sample.app.model.ToggleTodoAction;
import ru.noties.dedux.sample.app.model.ToggleTodoReducer;
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
        list.add(new AppBarState(application.getString(R.string.app_name), false, false, false, false));
        list.add(new InputState());
        list.add(new TodosState());
        return list;
    }

    private Reducer<Action> reducer() {
        return new ReducerBuilder()
                .add(InputAction.class, new InputReducer())
                .add(AddTodoAction.class, new AddTodoReducer())
                .add(ToggleTodoAction.class, new ToggleTodoReducer())
                .add(CheckDoneAction.class, new CheckDoneReducer())
                .add(ToggleAllDoneAction.class, new ToggleAllDoneReducer())
                .add(ClearDoneAction.class, new ClearDoneReducer())
                .build((s, a) -> {});
    }

    private Middleware<Action> middleware() {
        return new MiddlewareBuilder()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s, currentState: %s", action, store.state().state());
                    next.next();
                }))
                .add(ModifyTodoAction.class, new ModifyTodoMiddleware())
                .build();
    }
}
