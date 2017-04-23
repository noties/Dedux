package ru.noties.todo.app.todo.core;

public class ToggleTodoAction implements ModifyTodoAction {

    private final long id;

    public ToggleTodoAction(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
