package ru.noties.todo;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.MiddlewareBuilder;
import dedux.MutableState;
import dedux.Reducer;
import dedux.ReducerBuilder;
import dedux.Store;
import dedux.StoreBuilder;
import ru.noties.debug.Debug;
import ru.noties.todo.app.account.state.AccountAuthState;
import ru.noties.todo.app.appbar.state.AppBarState;
import ru.noties.todo.app.navigation.confirm.ConfirmAction;
import ru.noties.todo.app.navigation.confirm.ConfirmMiddleware;
import ru.noties.todo.app.todo.input.InputAction;
import ru.noties.todo.app.todo.input.InputReducer;
import ru.noties.todo.app.todo.input.InputState;
import ru.noties.todo.app.components.list.ScrollAction;
import ru.noties.todo.app.components.list.ScrollReducer;
import ru.noties.todo.app.navigation.core.NavigationState;
import ru.noties.todo.app.account.actions.AccountAuthStateChangedAction;
import ru.noties.todo.app.account.actions.AccountEmailChangedAction;
import ru.noties.todo.app.account.reducers.AccountEmailChangedReducer;
import ru.noties.todo.app.todo.core.AddTodoAction;
import ru.noties.todo.app.todo.core.AddTodoReducer;
import ru.noties.todo.app.appbar.actions.AppBarCheckDoneAction;
import ru.noties.todo.app.appbar.reducers.AppBarCheckDoneReducer;
import ru.noties.todo.app.todo.core.ClearDoneAction;
import ru.noties.todo.app.todo.core.ClearDoneReducer;
import ru.noties.todo.app.navigation.core.NavigationCloseAccountAction;
import ru.noties.todo.app.navigation.core.NavigationCloseAccountReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmCloseAction;
import ru.noties.todo.app.navigation.confirm.ConfirmCloseReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneAction;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneReducer;
import ru.noties.todo.app.appbar.actions.AppBarCountDoneAction;
import ru.noties.todo.app.appbar.reducers.AppBarCountDoneReducer;
import ru.noties.todo.app.todo.core.ModifyTodoAction;
import ru.noties.todo.app.todo.core.ModifyTodoMiddleware;
import ru.noties.todo.app.navigation.core.NavigationOpenAccountAction;
import ru.noties.todo.app.navigation.core.NavigationOpenAccountReducer;
import ru.noties.todo.app.todo.core.TodosState;
import ru.noties.todo.app.todo.core.ToggleAllDoneAction;
import ru.noties.todo.app.todo.core.ToggleAllDoneReducer;
import ru.noties.todo.app.todo.core.ToggleTodoAction;
import ru.noties.todo.app.todo.core.ToggleTodoReducer;
import ru.noties.todo.sample.R;
import ru.noties.todo.state.StatePersistence;
import ru.noties.todo.state.StateSerializer;
import ru.noties.todo.state.action.FirebaseSyncAction;
import ru.noties.todo.state.middleware.FirebaseSyncMiddleware;
import ru.noties.todo.state.reducer.FirebaseSyncReducer;

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
        final boolean isAuthenticated = FirebaseAuth.getInstance().getCurrentUser() != null;
        store.dispatch(new AccountAuthStateChangedAction(isAuthenticated));

        return store;
    }

    private List<Object> initialState() {
        final List<Object> list = new ArrayList<>();
        list.add(new AppBarState().title(application.getString(R.string.app_name)));
        list.add(new InputState().hasFocus(true));
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
                .add(FirebaseSyncAction.class, new FirebaseSyncReducer())
                .add(AccountAuthStateChangedAction.class, new Reducer<AccountAuthStateChangedAction>() {
                    @Override
                    public void reduce(@Nonnull MutableState state, @Nonnull AccountAuthStateChangedAction accountAuthStateChangedAction) {
                        // temp one
                    }
                })
                .build();
    }

    private Middleware<Action> middleware(@Nonnull StateSerializer stateSerializer) {
        return new MiddlewareBuilder()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s", action);
                    next.next();
                }))
                .add(ModifyTodoAction.class, new ModifyTodoMiddleware())
                .add(ConfirmAction.class, new ConfirmMiddleware())
                .add(AccountAuthStateChangedAction.class, new FirebaseSyncMiddleware(stateSerializer, firebaseAcceptedKeys()))
                .build();
    }

    @Nonnull
    private static Set<String> firebaseAcceptedKeys() {
        // we can change the states that we accept here,
        // to even include all of them, so with synchronization
        // all the app state will be synchronized (appBar, scroll, input, etc)
        // but it can lead to weird experience (if app is used by multiple people)
        return Collections.unmodifiableSet(
                Collections.singleton(TodosState.class.getName())
        );
    }
}
