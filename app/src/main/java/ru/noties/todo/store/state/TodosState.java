package ru.noties.todo.store.state;

import java.util.List;

import dedux.StateItemBase;
import ru.noties.todo.model.Todo;

public class TodosState extends StateItemBase {

    private List<Todo> todos;
    private transient boolean scrollToLast;

    public TodosState() {}

    public TodosState(List<Todo> todos) {
        this.todos = todos;
    }

    public List<Todo> todos() {
        return todos;
    }

    public TodosState todos(List<Todo> todos) {
        this.todos = todos;
        return this;
    }

    public boolean scrollToLast() {
        return scrollToLast;
    }

    public TodosState scrollToLast(boolean scrollToLast) {
        this.scrollToLast = scrollToLast;
        return this;
    }

    @Override
    public String toString() {
        return "TodosState{" +
                "todos=" + todos +
                ", scrollToLast=" + scrollToLast +
                '}';
    }
}
