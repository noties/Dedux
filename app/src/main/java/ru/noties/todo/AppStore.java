package ru.noties.todo;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.MiddlewareBuilder;
import dedux.Reducer;
import dedux.ReducerBuilder;
import dedux.Store;
import dedux.StoreBuilder;
import ru.noties.debug.Debug;
import ru.noties.todo.app.components.CountDoneState;
import ru.noties.todo.app.components.account.AccountAuthState;
import ru.noties.todo.app.components.appbar.AppBarState;
import ru.noties.todo.app.components.confirm.ConfirmAction;
import ru.noties.todo.app.components.confirm.ConfirmMiddleware;
import ru.noties.todo.app.components.input.InputAction;
import ru.noties.todo.app.components.input.InputReducer;
import ru.noties.todo.app.components.input.InputState;
import ru.noties.todo.app.components.list.ScrollAction;
import ru.noties.todo.app.components.list.ScrollReducer;
import ru.noties.todo.app.components.navigation.NavigationState;
import ru.noties.todo.app.model.AccountAuthStateChangedAction;
import ru.noties.todo.app.model.AccountEmailChangedAction;
import ru.noties.todo.app.model.AccountEmailChangedReducer;
import ru.noties.todo.app.model.AddTodoAction;
import ru.noties.todo.app.model.AddTodoReducer;
import ru.noties.todo.app.model.CheckDoneAction;
import ru.noties.todo.app.model.CheckDoneReducer;
import ru.noties.todo.app.model.ClearDoneAction;
import ru.noties.todo.app.model.ClearDoneReducer;
import ru.noties.todo.app.model.CloseAccountAction;
import ru.noties.todo.app.model.CloseAccountReducer;
import ru.noties.todo.app.model.CloseConfirmAction;
import ru.noties.todo.app.model.CloseConfirmReducer;
import ru.noties.todo.app.model.ConfirmClearDoneAction;
import ru.noties.todo.app.model.ConfirmClearDoneReducer;
import ru.noties.todo.app.model.CountDoneAction;
import ru.noties.todo.app.model.CountDoneReducer;
import ru.noties.todo.app.model.ModifyTodoAction;
import ru.noties.todo.app.model.ModifyTodoMiddleware;
import ru.noties.todo.app.model.OpenAccountAction;
import ru.noties.todo.app.model.OpenAccountReducer;
import ru.noties.todo.app.model.TodosState;
import ru.noties.todo.app.model.ToggleAllDoneAction;
import ru.noties.todo.app.model.ToggleAllDoneReducer;
import ru.noties.todo.app.model.ToggleTodoAction;
import ru.noties.todo.app.model.ToggleTodoReducer;
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
                .add(CheckDoneAction.class, new CheckDoneReducer())
                .add(ToggleAllDoneAction.class, new ToggleAllDoneReducer())
                .add(ConfirmClearDoneAction.class, new ConfirmClearDoneReducer(application.getResources()))
                .add(ClearDoneAction.class, new ClearDoneReducer())
                .add(OpenAccountAction.class, new OpenAccountReducer())
                .add(CloseAccountAction.class, new CloseAccountReducer())
                .add(AccountEmailChangedAction.class, new AccountEmailChangedReducer())
                .add(CountDoneAction.class, new CountDoneReducer())
                .add(CloseConfirmAction.class, new CloseConfirmReducer())
                .add(ScrollAction.class, new ScrollReducer())
                .add(FirebaseSyncAction.class, new FirebaseSyncReducer())
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
