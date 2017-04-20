package ru.noties.tddd.app.model;

import java.util.List;

import ru.noties.tddd.data.Todo;
import ru.noties.tddd.state.BaseState;

public class TodosState extends BaseState {

    private List<Todo> todos;

    public TodosState() {}

    public TodosState(List<Todo> todos) {
        this.todos = todos;
    }

    public List<Todo> todos() {
        return todos;
    }

    @Override
    public String toString() {
        return "TodosState{" +
                "todos=" + todos +
                '}';
    }
}
