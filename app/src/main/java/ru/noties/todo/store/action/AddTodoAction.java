package ru.noties.todo.store.action;

public class AddTodoAction implements ModifyTodoAction {

    private final String name;

    public AddTodoAction(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
