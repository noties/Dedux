package ru.noties.todo.app.todo.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.appbar.AppBarState;
import ru.noties.todo.utils.CollectionUtils;

public class ToggleAllDoneReducer implements Reducer<ToggleAllDoneAction> {

    @Nonnull
    @Override
    public Class<ToggleAllDoneAction> actionType() {
        return ToggleAllDoneAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ToggleAllDoneAction toggleAllDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        if (CollectionUtils.isEmpty(todosState.todos())) {
            return;
        }

        final AppBarState appBarState = state.get(AppBarState.class).get();
        final boolean out = !appBarState.allDone();
        final List<Todo> toggled = setAll(out, todosState.todos());

        state.set(new TodosState(toggled));
    }

    private static List<Todo> setAll(boolean isDone, List<Todo> list) {
        final List<Todo> out = new ArrayList<>(list.size());
        for (Todo todo : list) {
            out.add(new Todo(todo.id(), todo.getName(), isDone));
        }
        return out;
    }
}
