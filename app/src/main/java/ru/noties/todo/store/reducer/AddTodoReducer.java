package ru.noties.todo.store.reducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.model.Todo;
import ru.noties.todo.store.state.InputState;
import ru.noties.todo.store.state.TodosState;
import ru.noties.todo.store.action.AddTodoAction;
import ru.noties.todo.utils.CollectionUtils;

public class AddTodoReducer implements Reducer<AddTodoAction> {

    @Nonnull
    @Override
    public Class<AddTodoAction> actionType() {
        return AddTodoAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AddTodoAction addTodoAction) {

        final Todo todo = new Todo(System.currentTimeMillis(), addTodoAction.name(), false);

        final TodosState currentState = state.get(TodosState.class).get();

        final List<Todo> list;
        if (CollectionUtils.isEmpty(currentState.todos())) {
            list = Collections.singletonList(todo);
        } else {
            list = new ArrayList<>(currentState.todos());
            list.add(todo);
        }

        final TodosState out = new TodosState()
                .todos(list)
                .scrollToLast(true);

        state.set(out);

        final InputState inputState = new InputState(true, null, 0, 0);
        state.set(inputState);
    }
}
