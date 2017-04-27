package ru.noties.todo;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.Action;
import dedux.Middleware;
import dedux.Reducer;
import dedux.StateItem;
import dedux.Store;
import dedux.builders.MiddlewareBuilder;
import dedux.builders.ReducerBuilder;
import dedux.builders.StoreBuilder;
import ru.noties.debug.Debug;
import ru.noties.todo.app.appbar.AppBarCheckDoneReducer;
import ru.noties.todo.app.appbar.AppBarCountDoneReducer;
import ru.noties.todo.app.appbar.AppBarState;
import ru.noties.todo.app.navigation.confirm.ConfirmAction;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmCloseReducer;
import ru.noties.todo.app.navigation.confirm.ConfirmMiddleware;
import ru.noties.todo.app.todo.core.AddTodoReducer;
import ru.noties.todo.app.todo.core.ClearDoneReducer;
import ru.noties.todo.app.todo.core.ModifyTodoAction;
import ru.noties.todo.app.todo.core.ModifyTodoMiddleware;
import ru.noties.todo.app.todo.core.ToggleAllDoneReducer;
import ru.noties.todo.app.todo.core.ToggleTodoReducer;
import ru.noties.todo.app.todo.input.InputReducer;
import ru.noties.todo.app.todo.list.ScrollReducer;

class AppStore {

    static Store create(@Nonnull Application application) {
        return new AppStore(application).create();
    }

    private final Application application;

    private AppStore(@Nonnull Application application) {
        this.application = application;
    }

    private Store create() {

        final SharedPreferences preferences
                = application.getSharedPreferences("pasa-pasa", Application.MODE_PRIVATE);

        return StoreBuilder.create()
                .storage(new AppStorage(preferences, initialState()))
                .reducer(reducer())
                .middleware(middleware())
                .build();
    }

    private List<? extends StateItem> initialState() {
        final List<StateItem> list = new ArrayList<>();
        list.add(new AppBarState().title(application.getString(R.string.app_bar_title)));
        return list;
    }

    private Reducer<Action> reducer() {

        // can register each reducer individually or in a list
        final List<Reducer<? extends Action>> reducers = Arrays.asList(new InputReducer(), new AddTodoReducer());

        // order in which reducers are added doesn't matter

        return ReducerBuilder.create()
//                .add(new InputReducer())
//                .add(new AddTodoReducer())
                .add(new ToggleTodoReducer())
                .add(new AppBarCheckDoneReducer())
                .add(new ToggleAllDoneReducer())
                .add(new ConfirmClearDoneReducer(application.getResources()))
                .add(new ClearDoneReducer())
                .add(new AppBarCountDoneReducer())
                .add(new ConfirmCloseReducer())
                .add(new ScrollReducer())
                .addAll(reducers)
                .build();
    }

    private Middleware<Action> middleware() {
        return MiddlewareBuilder.create()
                .add(Action.class, ((store, action, next) -> {
                    Debug.i("action: %s", action);
                    next.next();
                }))
                .add(ModifyTodoAction.class, new ModifyTodoMiddleware())
                .add(ConfirmAction.class, new ConfirmMiddleware())
                .build();
    }
}
