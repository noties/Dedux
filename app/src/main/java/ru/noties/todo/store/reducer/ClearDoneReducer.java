package ru.noties.todo.store.reducer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.model.Todo;
import ru.noties.todo.store.state.TodosState;
import ru.noties.todo.store.action.ClearDoneAction;
import ru.noties.todo.utils.CollectionUtils;

public class ClearDoneReducer implements Reducer<ClearDoneAction> {

    @Nonnull
    @Override
    public Class<ClearDoneAction> actionType() {
        return ClearDoneAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ClearDoneAction clearDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        if (CollectionUtils.isEmpty(todosState.todos())) {
            return;
        }

        final List<Todo> todos = todosState.todos();
        final List<Todo> out = new ArrayList<>();
        for (Todo todo : todos) {
            if (!todo.isDone()) {
                out.add(todo);
            }
        }

        state.set(new TodosState(out));
    }
}
