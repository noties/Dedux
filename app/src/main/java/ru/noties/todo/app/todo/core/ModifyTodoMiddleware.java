package ru.noties.todo.app.todo.core;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import ru.noties.todo.app.appbar.AppBarCheckDoneAction;
import ru.noties.todo.app.appbar.AppBarCountDoneAction;

public class ModifyTodoMiddleware implements Middleware<ModifyTodoAction> {

    @Override
    public void apply(@Nonnull Store store, @Nonnull ModifyTodoAction action, @Nonnull Next next) {
        // due to the fact that this middleware (in general) can do some async work,
        // maybe it's better to provide a, for example, `Done` callback?
        // which will be called after action is finally reduced?
        next.next();
        store.dispatch(new AppBarCountDoneAction());
        store.dispatch(new AppBarCheckDoneAction());
    }
}
