package ru.noties.tddd.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.tddd.data.Todo;
import ru.noties.tddd.utils.CollectionUtils;

public class ClearDoneReducer implements Reducer<ClearDoneAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ClearDoneAction clearDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        if (todosState == null
                || CollectionUtils.isEmpty(todosState.todos())) {
            return;
        }

        final List<Todo> todos = todosState.todos();
        final List<Todo> out = new ArrayList<>();
        for (Todo todo: todos) {
            if (!todo.isDone()) {
                out.add(todo);
            }
        }

        state.set(new TodosState(out));
    }
}
