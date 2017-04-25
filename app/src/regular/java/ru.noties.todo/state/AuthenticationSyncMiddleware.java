package ru.noties.todo.state;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Middleware;
import dedux.StateItem;
import dedux.Store;
import ru.noties.todo.app.account.AccountAuthStateChangedAction;

public class AuthenticationSyncMiddleware implements Middleware<AccountAuthStateChangedAction> {

    public AuthenticationSyncMiddleware(StateSerializer stateSerializer, @Nullable Set<Class<? extends StateItem>> acceptedKeys) {
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull AccountAuthStateChangedAction action, @Nonnull Next next) {
        // no op
        next.next();
    }
}