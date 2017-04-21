package ru.noties.tddd;

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
import ru.noties.tddd.app.components.account.AccountAuthState;
import ru.noties.tddd.app.components.appbar.AppBarState;
import ru.noties.tddd.app.components.confirm.ConfirmAction;
import ru.noties.tddd.app.components.confirm.ConfirmMiddleware;
import ru.noties.tddd.app.components.input.InputAction;
import ru.noties.tddd.app.components.input.InputReducer;
import ru.noties.tddd.app.components.input.InputState;
import ru.noties.tddd.app.components.navigation.NavigationState;
import ru.noties.tddd.app.model.AccountEmailChangedAction;
import ru.noties.tddd.app.model.AccountEmailChangedReducer;
import ru.noties.tddd.app.model.AddTodoAction;
import ru.noties.tddd.app.model.AddTodoReducer;
import ru.noties.tddd.app.model.CheckDoneAction;
import ru.noties.tddd.app.model.CheckDoneReducer;
import ru.noties.tddd.app.model.ClearDoneAction;
import ru.noties.tddd.app.model.ClearDoneReducer;
import ru.noties.tddd.app.model.CloseAccountAction;
import ru.noties.tddd.app.model.CloseAccountReducer;
import ru.noties.tddd.app.model.CloseConfirmAction;
import ru.noties.tddd.app.model.CloseConfirmReducer;
import ru.noties.tddd.app.model.ConfirmClearDoneAction;
import ru.noties.tddd.app.model.ConfirmClearDoneReducer;
import ru.noties.tddd.app.model.CountDoneAction;
import ru.noties.tddd.app.model.CountDoneReducer;
import ru.noties.tddd.app.model.ModifyTodoAction;
import ru.noties.tddd.app.model.ModifyTodoMiddleware;
import ru.noties.tddd.app.model.OpenAccountAction;
import ru.noties.tddd.app.model.OpenAccountReducer;
import ru.noties.tddd.app.model.TodosState;
import ru.noties.tddd.app.model.ToggleAllDoneAction;
import ru.noties.tddd.app.model.ToggleAllDoneReducer;
import ru.noties.tddd.app.model.ToggleTodoAction;
import ru.noties.tddd.app.model.ToggleTodoReducer;
import ru.noties.tddd.sample.R;
import ru.noties.tddd.state.StatePersistence;

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
        list.add(new AppBarState().title(application.getString(R.string.app_name)));
        list.add(new InputState());
        list.add(new TodosState());
        list.add(new NavigationState().showApp(true).showAccount(false));
        list.add(new AccountAuthState());
        return list;
    }

    private Reducer<Action> reducer() {
        return new ReducerBuilder()
                .add(InputAction.class, new InputReducer())
                .add(AddTodoAction.class, new AddTodoReducer())
                .add(ToggleTodoAction.class, new ToggleTodoReducer())
                .add(CheckDoneAction.class, new CheckDoneReducer())
                .add(ToggleAllDoneAction.class, new ToggleAllDoneReducer())
                .add(ConfirmClearDoneAction.class, new ConfirmClearDoneReducer(application.getResources()))
                .add(ClearDoneAction.class, new ClearDoneReducer())
                .add(OpenAccountAction.class, new OpenAccountReducer())
                .add(CloseAccountAction.class, new CloseAccountReducer())
                .add(AccountEmailChangedAction.class, new AccountEmailChangedReducer())
                .add(CountDoneAction.class, new CountDoneReducer())
                .add(CloseConfirmAction.class, new CloseConfirmReducer())
                .build();
    }

    private Middleware<Action> middleware() {
        return new MiddlewareBuilder()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s", action);
                    next.next();
                }))
                .add(ModifyTodoAction.class, new ModifyTodoMiddleware())
                .add(ConfirmAction.class, new ConfirmMiddleware())
                .build();
    }
}
