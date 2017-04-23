package ru.noties.todo.app.model;

import dedux.Action;

public class AccountEmailChangedAction implements Action {

    private final String email;

    public AccountEmailChangedAction(String email) {
        this.email = email;
    }

    public String email() {
        return email;
    }

    @Override
    public String toString() {
        return "AccountEmailChangedAction{" +
                "email='" + email + '\'' +
                '}';
    }
}
