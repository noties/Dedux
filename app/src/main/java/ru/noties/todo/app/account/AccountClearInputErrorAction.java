package ru.noties.todo.app.account;

import dedux.Action;

public class AccountClearInputErrorAction implements Action {

    private final boolean email;
    private final boolean password;

    public AccountClearInputErrorAction(boolean email, boolean password) {
        this.email = email;
        this.password = password;
    }

    public boolean email() {
        return email;
    }

    public boolean password() {
        return password;
    }

    @Override
    public String toString() {
        return "AccountClearInputErrorAction{" +
                "email=" + email +
                ", password=" + password +
                '}';
    }
}
