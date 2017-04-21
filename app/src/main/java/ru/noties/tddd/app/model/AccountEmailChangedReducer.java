package ru.noties.tddd.app.model;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.tddd.app.components.account.AccountAuthState;

public class AccountEmailChangedReducer implements Reducer<AccountEmailChangedAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AccountEmailChangedAction accountEmailChangedAction) {
        final AccountAuthState authState = state.get(AccountAuthState.class).get();
        final AccountAuthState out = authState.clone(in -> in.email(accountEmailChangedAction.email()));
        state.set(out);
    }
}
