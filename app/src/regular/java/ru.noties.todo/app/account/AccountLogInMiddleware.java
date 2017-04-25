package ru.noties.todo.app.account;

import android.content.res.Resources;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;

public class AccountLogInMiddleware implements Middleware<AccountLogInAction> {

    public AccountLogInMiddleware(Resources resources) {
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull AccountLogInAction action, @Nonnull Next next) {
        next.next();
    }
}