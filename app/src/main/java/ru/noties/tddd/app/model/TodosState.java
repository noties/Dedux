package ru.noties.tddd.app.model;

import java.util.List;

import ru.noties.tddd.data.Todo;
import ru.noties.tddd.state.BaseState;

public class TodosState extends BaseState {

    private List<Todo> todos;
    private boolean scrollToLast;

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
