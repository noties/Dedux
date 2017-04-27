package ru.noties.todo.store;

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
import ru.noties.todo.R;
import ru.noties.todo.store.middleware.ConfirmMiddleware;
import ru.noties.todo.store.middleware.ModifyTodoMiddleware;
import ru.noties.todo.store.reducer.AddTodoReducer;
import ru.noties.todo.store.reducer.AppBarCheckDoneReducer;
import ru.noties.todo.store.reducer.AppBarCountDoneReducer;
import ru.noties.todo.store.reducer.ClearDoneReducer;
import ru.noties.todo.store.reducer.ConfirmClearDoneReducer;
import ru.noties.todo.store.reducer.ConfirmCloseReducer;
import ru.noties.todo.store.reducer.InputReducer;
import ru.noties.todo.store.reducer.ScrollReducer;
import ru.noties.todo.store.reducer.ToggleAllDoneReducer;
import ru.noties.todo.store.reducer.ToggleTodoReducer;
import ru.noties.todo.store.state.AppBarState;

public class AppStore {

    public static Store create(@Nonnull Application application) {
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

        // can register each reducer individually or supply a list of reducers
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

        // Middleware can be registered independently and as a list
        // order matters - it defines order in which middlewares will be called in a chain

        final List<Middleware<? extends Action>> list = Arrays.asList(new ModifyTodoMiddleware(), new ConfirmMiddleware());

        return MiddlewareBuilder.create()
                .add(new LoggingMiddleware())
//                .add(new ModifyTodoMiddleware())
//                .add(new ConfirmMiddleware())
                .addAll(list)
                .build();
    }

    private static class LoggingMiddleware implements Middleware<Action> {

        @Nonnull
        @Override
        public Class<Action> actionType() {
            return Action.class;
        }

        @Override
        public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull Next next) {
            Debug.i("action: %s", action);
            next.next();
        }
    }
}
