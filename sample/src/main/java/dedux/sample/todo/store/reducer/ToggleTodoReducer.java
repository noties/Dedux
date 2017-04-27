package dedux.sample.todo.store.reducer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.ToggleTodoAction;

public class ToggleTodoReducer implements Reducer<ToggleTodoAction> {

    @Nonnull
    @Override
    public Class<ToggleTodoAction> actionType() {
        return ToggleTodoAction.class;
    }

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
