package ru.noties.todo.app.model;

import dedux.Action;

public class AccountAuthStateChangedAction implements Action {

    private final boolean isAuthernticated;

    public AccountAuthStateChangedAction(boolean isAuthernticated) {
        this.isAuthernticated = isAuthernticated;
    }

    public boolean isAuthernticated() {
        return isAuthernticated;
    }

    @Override
    public String toString() {
        return "AccountAuthStateChangedAction{" +
                "isAuthernticated=" + isAuthernticated +
                '}';
    }
}
