package ru.noties.dedux.sample.app.model;

import dedux.Action;

public class AddTodoAction implements Action {

    private final String name;

    public AddTodoAction(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
