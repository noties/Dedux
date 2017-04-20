package ru.noties.dedux.sample.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.dedux.sample.app.components.input.InputState;
import ru.noties.dedux.sample.data.Todo;
import ru.noties.dedux.sample.utils.CollectionUtils;

public class AddTodoReducer implements Reducer<AddTodoAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AddTodoAction addTodoAction) {

        // erase current input state after we have saved the todo

        final Todo todo = new Todo(System.currentTimeMillis(), addTodoAction.name(), false);

        final TodosState currentState = state.get(TodosState.class).get();

        final List<Todo> list;
        if (CollectionUtils.isEmpty(currentState.todos())) {
            list = Collections.singletonList(todo);
        } else {
            list = new ArrayList<>(currentState.todos());
            list.add(todo);
        }

        final TodosState out = new TodosState(list);
        state.set(out);

        final InputState inputState = new InputState(false, null, 0, 0);
        state.set(inputState);
    }
}
