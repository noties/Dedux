package dedux.sample.todo.store;

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
import dedux.sample.todo.R;
import dedux.sample.todo.store.reducer.EditTodoReducer;
import ru.noties.debug.Debug;
import dedux.sample.todo.store.middleware.ConfirmMiddleware;
import dedux.sample.todo.store.middleware.ModifyTodoMiddleware;
import dedux.sample.todo.store.reducer.AddTodoReducer;
import dedux.sample.todo.store.reducer.AppBarCheckDoneReducer;
import dedux.sample.todo.store.reducer.AppBarCountDoneReducer;
import dedux.sample.todo.store.reducer.ClearDoneReducer;
import dedux.sample.todo.store.reducer.ConfirmClearDoneReducer;
import dedux.sample.todo.store.reducer.ConfirmCloseReducer;
import dedux.sample.todo.store.reducer.InputReducer;
import dedux.sample.todo.store.reducer.ScrollReducer;
import dedux.sample.todo.store.reducer.ToggleAllDoneReducer;
import dedux.sample.todo.store.reducer.ToggleTodoReducer;
import dedux.sample.todo.store.state.AppBarState;

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
        // generally speaking it feels wrong to have `Android` specifics here.
        // also, generally, we should avoid using these specifics too much, and
        // absolutely **must** avoid them if we are build cross-platform applications.
        // but as this application is Android only (for now, of cause!) we can skip
        // creating unneeded right now abstraction for system resources
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
                .add(new EditTodoReducer())
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
