package ru.noties.todo;

import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.Reducer;
import dedux.StateItem;
import dedux.Store;
import dedux.StoreBuilder;
import dedux.builders.MiddlewareBuilder;
import dedux.builders.ReducerBuilder;
import ru.noties.debug.Debug;
import ru.noties.todo.app.account.AccountAuthAction;
import ru.noties.todo.app.account.AccountAuthReducer;
import ru.noties.todo.app.account.AccountAuthStateChangedAction;
import ru.noties.todo.app.account.AccountAuthStateChangedReducer;
import ru.noties.todo.app.account.AccountClearInputErrorAction;
import ru.noties.todo.app.account.AccountClearInputErrorReducer;
import ru.noties.todo.app.account.AccountEmailChangedAction;
import ru.noties.todo.app.account.AccountEmailChangedReducer;
import ru.noties.todo.app.account.AccountLogInAction;
import ru.noties.todo.app.account.AccountLogInMiddleware;
import ru.noties.todo.app.appbar.AppBarCheckDoneAction;
import ru.noties.todo.app.appbar.AppBarCheckDoneReducer;
import ru.noties.todo.app.appbar.AppBarCountDoneAction;
import ru.noties.todo.app.appbar.AppBarCountDoneReducer;
import ru.noties.todo.app.appbar.AppBarState;
import ru.noties.todo.app.navigation.confirm.ConfirmAction;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneAction;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmCloseAction;
import ru.noties.todo.app.navigation.confirm.ConfirmCloseReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmMiddleware;
import ru.noties.todo.app.navigation.core.NavigationCloseAccountAction;
import ru.noties.todo.app.navigation.core.NavigationCloseAccountReducer;
import ru.noties.todo.app.navigation.core.NavigationOpenAccountAction;
import ru.noties.todo.app.navigation.core.NavigationOpenAccountReducer;
import ru.noties.todo.app.todo.core.AddTodoAction;
import ru.noties.todo.app.todo.core.AddTodoReducer;
import ru.noties.todo.app.todo.core.ClearDoneAction;
import ru.noties.todo.app.todo.core.ClearDoneReducer;
import ru.noties.todo.app.todo.core.ModifyTodoAction;
import ru.noties.todo.app.todo.core.ModifyTodoMiddleware;
import ru.noties.todo.app.todo.core.TodosState;
import ru.noties.todo.app.todo.core.ToggleAllDoneAction;
import ru.noties.todo.app.todo.core.ToggleAllDoneReducer;
import ru.noties.todo.app.todo.core.ToggleTodoAction;
import ru.noties.todo.app.todo.core.ToggleTodoReducer;
import ru.noties.todo.app.todo.input.InputAction;
import ru.noties.todo.app.todo.input.InputReducer;
import ru.noties.todo.app.todo.list.ScrollAction;
import ru.noties.todo.app.todo.list.ScrollReducer;
import ru.noties.todo.state.AuthenticationSyncAction;
import ru.noties.todo.state.AuthenticationSyncMiddleware;
import ru.noties.todo.state.AuthenticationSyncReducer;
import ru.noties.todo.state.StatePersistence;
import ru.noties.todo.state.StateSerializer;

class AppStore {

    static Store create(@Nonnull Application application) {
        return new AppStore(application).create();
    }

    private final Application application;

    private AppStore(@Nonnull Application application) {
        this.application = application;
    }

    private Store create() {

        final StateSerializer stateSerializer = new StateSerializer();
        final StatePersistence persistence = new StatePersistence(application.getApplicationContext(), stateSerializer, initialState());

        final Store store = new StoreBuilder()
                .preloadedState(persistence.preloadedState())
                .middleware(middleware(stateSerializer))
                .build(reducer());

        persistence.onStoreCreated(store);

        // check if we are authenticated and dispatch event
        final boolean isAuthenticated = AppAuthentication.isAuthenticated();
        Debug.i("isAuthenticated: %s", isAuthenticated);
        store.dispatch(new AccountAuthStateChangedAction(isAuthenticated));

        return store;
    }

    private List<? extends StateItem> initialState() {
        final List<StateItem> list = new ArrayList<>();
        list.add(new AppBarState().title(application.getString(R.string.app_name)));
        return list;
    }

    private Reducer<Action> reducer() {
        return ReducerBuilder.create()
                .add(InputAction.class, new InputReducer())
                .add(AddTodoAction.class, new AddTodoReducer())
                .add(ToggleTodoAction.class, new ToggleTodoReducer())
                .add(AppBarCheckDoneAction.class, new AppBarCheckDoneReducer())
                .add(ToggleAllDoneAction.class, new ToggleAllDoneReducer())
                .add(ConfirmClearDoneAction.class, new ConfirmClearDoneReducer(application.getResources()))
                .add(ClearDoneAction.class, new ClearDoneReducer())
                .add(NavigationOpenAccountAction.class, new NavigationOpenAccountReducer())
                .add(NavigationCloseAccountAction.class, new NavigationCloseAccountReducer())
                .add(AccountEmailChangedAction.class, new AccountEmailChangedReducer())
                .add(AppBarCountDoneAction.class, new AppBarCountDoneReducer())
                .add(ConfirmCloseAction.class, new ConfirmCloseReducer())
                .add(ScrollAction.class, new ScrollReducer())
                .add(AuthenticationSyncAction.class, new AuthenticationSyncReducer())
                .add(AccountAuthAction.class, new AccountAuthReducer())
                .add(AccountAuthStateChangedAction.class, new AccountAuthStateChangedReducer())
                .add(AccountClearInputErrorAction.class, new AccountClearInputErrorReducer())
                .build();
    }

    private Middleware<Action> middleware(@Nonnull StateSerializer stateSerializer) {
        return MiddlewareBuilder.create()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s", action);
                    next.next();
                }))
                .add(ModifyTodoAction.class, new ModifyTodoMiddleware())
                .add(ConfirmAction.class, new ConfirmMiddleware())
                .add(AccountAuthStateChangedAction.class, new AuthenticationSyncMiddleware(stateSerializer, firebaseAcceptedKeys()))
                .add(AccountLogInAction.class, new AccountLogInMiddleware(application.getResources()))
                .build();
    }

    // if `null` is returned then all the states will be persisted to firebase
    @Nullable
    private static Set<Class<? extends StateItem>> firebaseAcceptedKeys() {
        // we can change the states that we accept here,
        // to even include all of them, so with synchronization
        // all the app state will be synchronized (appBar, scroll, input, etc)
        // but it can lead to weird experience (if app is used by multiple people)
        //
        // but saving all possible state will let us to have a continuous experience between
        // different devices and/or different platforms
        return Collections.unmodifiableSet(
                Collections.singleton(TodosState.class)
        );
    }
}
