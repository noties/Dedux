package ru.noties.todo.store.action;

public class ToggleTodoAction implements ModifyTodoAction {

    private final long id;

    public ToggleTodoAction(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }
}
