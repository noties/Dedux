package ru.noties.dedux.sample.app.model;

import dedux.Action;

public class ToggleTodoAction implements Action {

    private final long id;

    public ToggleTodoAction(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
