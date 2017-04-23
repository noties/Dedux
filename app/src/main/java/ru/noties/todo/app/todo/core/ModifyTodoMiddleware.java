package ru.noties.todo.app.todo.core;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import ru.noties.todo.app.appbar.actions.AppBarCheckDoneAction;
import ru.noties.todo.app.appbar.actions.AppBarCountDoneAction;

public class ModifyTodoMiddleware implements Middleware<ModifyTodoAction> {

    @Override
    public void apply(@Nonnull Store store, @Nonnull ModifyTodoAction action, @Nonnull Next next) {
        next.next();
        store.dispatch(new AppBarCountDoneAction());
        store.dispatch(new AppBarCheckDoneAction());
    }
}
