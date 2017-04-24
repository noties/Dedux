package ru.noties.todo.app.account;

import javax.annotation.Nullable;

import dedux.Action;

public class AccountLogInAction implements Action {

    @Nullable
    private final String email;

    @Nullable
    private final String password;

    public AccountLogInAction(@Nullable String email, @Nullable String password) {
        this.email = email;
        this.password = password;
    }

    @Nullable
    public String email() {
        return email;
    }

    @Nullable
    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "AccountLogInAction{" +
                "email='" + email + '\'' +
                '}';
    }
}
