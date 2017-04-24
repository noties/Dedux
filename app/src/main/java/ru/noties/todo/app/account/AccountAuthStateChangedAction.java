package ru.noties.todo.app.account;

import dedux.Action;

public class AccountAuthStateChangedAction implements Action {

    private final boolean isAuthenticated;

    public AccountAuthStateChangedAction(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public String toString() {
        return "AccountAuthStateChangedAction{" +
                "isAuthenticated=" + isAuthenticated +
                '}';
    }
}
