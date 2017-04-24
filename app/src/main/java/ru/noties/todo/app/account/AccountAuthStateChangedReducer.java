package ru.noties.todo.app.account;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.appbar.AppBarState;

public class AccountAuthStateChangedReducer implements Reducer<AccountAuthStateChangedAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AccountAuthStateChangedAction accountAuthStateChangedAction) {

        final AccountAuthState authState = state.get(AccountAuthState.class).get();
        final AccountAuthState out = new AccountAuthState()
                .email(authState.email())
                .isLoggedIn(accountAuthStateChangedAction.isAuthenticated());
        state.set(out);

        final AppBarState appBarState = state.get(AppBarState.class).get();
        final AppBarState outAppBarState = appBarState.clone(in -> in.loggedIn(accountAuthStateChangedAction.isAuthenticated()));
        state.set(outAppBarState);
    }
}
