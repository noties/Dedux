package ru.noties.tddd.app.model;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;

public class ModifyTodoMiddleware implements Middleware<ModifyTodoAction> {

    @Override
    public void apply(@Nonnull Store store, @Nonnull ModifyTodoAction action, @Nonnull Next next) {
        next.next();
        store.dispatch(new CountDoneAction());
        store.dispatch(new CheckDoneAction());
    }
}