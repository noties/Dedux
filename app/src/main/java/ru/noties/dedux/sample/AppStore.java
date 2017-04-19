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
import ru.noties.dedux.sample.action.AppStateAction;
import ru.noties.dedux.sample.action.GoBackAction;
import ru.noties.dedux.sample.action.NavigatorAction;
import ru.noties.dedux.sample.state.AppBarState;
import ru.noties.dedux.sample.state.AppScreen;
import ru.noties.dedux.sample.state.AppState;
import ru.noties.dedux.sample.state.NavigatorState;

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
        list.add(new AppState());
        list.add(new AppBarState(false, "Todos"));
        return list;
    }

    private Reducer<Action> reducer() {
        return new ReducerBuilder()
                .add(NavigatorAction.class, (s, a) -> s.set(new NavigatorState(a.back, a.close)))
                .add(AppStateAction.class, (s, a) -> {
                    s.set(new AppState(a.screen));
                    final AppBarState appBarState;
                    switch (a.screen) {
                        case LIST:
                            appBarState = new AppBarState(false, "Todos");
                            break;
                        case INPUT:
                            appBarState = new AppBarState(true, "New");
                            break;
                        default:
                            throw new RuntimeException("Unknown screen: " + a.screen);
                    }
                    s.set(appBarState);
                })
                .build((s, a) -> {});
    }

    private Middleware<Action> middleware() {
        return new MiddlewareBuilder()
                .add(Action.class, new Middleware<Action>() {
                    @Override
                    public void apply(@Nonnull Store store, @Nonnull Action action, @Nonnull Next next) {
                        Debug.i("action: %s, state: %s", action, store.state().state());
                        next.next();
                    }
                })
                .add(GoBackAction.class, new Middleware<GoBackAction>() {
                    @Override
                    public void apply(@Nonnull Store store, @Nonnull GoBackAction action, @Nonnull Next next) {

                        // we query current state (screen)
                        final AppState appState = store.state().get(AppState.class).get();
                        if (appState != null) {
                            final boolean back;
                            final boolean close;
                            if (AppScreen.INPUT == appState.appScreen) {
                                back = true;
                                close = false;
                            } else {
                                back = false;
                                close = true;
                            }
                            store.dispatch(new NavigatorAction(back, close));
                            if (back) {
                                store.dispatch(new AppStateAction(AppScreen.LIST));
                            }
                        }

                        next.next();
                    }
                })
                .build();
    }
}
