package ru.noties.dedux.sample.app.model;

import java.util.List;

import ru.noties.dedux.sample.data.Todo;
import ru.noties.dedux.sample.state.BaseState;

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
