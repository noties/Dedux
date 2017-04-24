package ru.noties.todo.app.account;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class AccountClearInputErrorReducer implements Reducer<AccountClearInputErrorAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AccountClearInputErrorAction accountClearInputErrorAction) {
        final AccountAuthState authState = state.get(AccountAuthState.class).get();
        final AccountAuthState out = authState.clone(in -> {
            if (accountClearInputErrorAction.email()) {
                in.emailInputError(null);
            }
            if (accountClearInputErrorAction.password()) {
                in.passwordInputError(null);
            }
        });
        state.set(out);
    }
}
