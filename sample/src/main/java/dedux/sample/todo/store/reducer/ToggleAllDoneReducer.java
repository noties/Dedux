package dedux.sample.todo.store.reducer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.AppBarState;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.ToggleAllDoneAction;
import dedux.sample.todo.utils.CollectionUtils;

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
