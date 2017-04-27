package dedux.sample.todo.store.reducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.InputState;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.AddTodoAction;
import dedux.sample.todo.utils.CollectionUtils;

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
