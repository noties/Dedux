package ru.noties.todo.app.todo.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class ToggleTodoReducer implements Reducer<ToggleTodoAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ToggleTodoAction toggleTodoAction) {

        final long id = toggleTodoAction.id();
        final TodosState todosState = state.get(TodosState.class).get();
        final List<Todo> todos = todosState.todos();
        final int size = todos.size();
        final List<Todo> out = new ArrayList<>(size);
        Todo todo;
        for (int i = 0; i < size; i++) {
            todo = todos.get(i);
            if (id == todo.id()) {
                out.add(new Todo(id, todo.getName(), !todo.isDone()));
            } else {
                out.add(todo);
            }
        }
        state.set(new TodosState(out));
    }
}
