package ru.noties.dedux.sample.app.model;

public class ToggleTodoAction implements ModifyTodoAction {

    private final long id;

    public ToggleTodoAction(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
