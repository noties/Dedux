package ru.noties.todo.app.account;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class AccountAuthReducer implements Reducer<AccountAuthAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AccountAuthAction accountAuthAction) {
        final AccountAuthState authState = state.get(AccountAuthState.class).get();
        final AccountAuthState out = authState.clone(in -> {
            in.isLoggedIn(accountAuthAction.isLoggedIn());
            in.emailInputError(accountAuthAction.emailInputError());
            in.passwordInputError(accountAuthAction.passwordInputError());
            in.signUpFirst(accountAuthAction.signUpFirst());
        });
        state.set(out);
    }
}
