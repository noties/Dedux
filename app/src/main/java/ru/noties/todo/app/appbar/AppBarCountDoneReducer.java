package ru.noties.todo.app.appbar;

import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.todo.core.TodosState;
import ru.noties.todo.app.todo.core.Todo;
import ru.noties.todo.utils.CollectionUtils;

public class AppBarCountDoneReducer implements Reducer<AppBarCountDoneAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AppBarCountDoneAction appBarCountDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        final AppBarState appBarState = state.get(AppBarState.class).get();

        final int done = count(todosState);
        if (appBarState == null
                || done != appBarState.doneCount()) {
            final AppBarState out;
            if (appBarState != null) {
                out = appBarState.clone(in -> in.doneCount(done));
            } else {
                out = new AppBarState().doneCount(done);
            }
            state.set(out);
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
            for (Todo todo : todos) {
                if (todo.isDone()) {
                    inner += 1;
                }
            }
            out = inner;
        }
        return out;
    }
}
