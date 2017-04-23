package ru.noties.todo.app.model;

import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.components.CountDoneState;
import ru.noties.todo.data.Todo;
import ru.noties.todo.utils.CollectionUtils;

public class CountDoneReducer implements Reducer<CountDoneAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull CountDoneAction countDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        final CountDoneState countDoneState = state.get(CountDoneState.class).get();

        final int done = count(todosState);
        if (countDoneState == null
                || done != countDoneState.count()) {
            state.set(new CountDoneState(done));
        }
    }

    private static int count(TodosState state) {
        final int out;
        if (state == null
                || CollectionUtils.isEmpty(state.todos())) {
            out = 0;
        } else {
            int inner = 0;
            final List<Todo> todos = state.todos();
            for (Todo todo: todos) {
                if (todo.isDone()) {
                    inner += 1;
                }
            }
            out = inner;
        }
        return out;
    }
}
