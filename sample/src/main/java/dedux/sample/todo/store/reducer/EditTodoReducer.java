package dedux.sample.todo.store.reducer;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.action.EditTodoAction;
import dedux.sample.todo.store.state.InputState;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.utils.CollectionUtils;

public class EditTodoReducer implements Reducer<EditTodoAction> {

    @Nonnull
    @Override
    public Class<EditTodoAction> actionType() {
        return EditTodoAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull EditTodoAction editTodoAction) {

        // the logic:
        // check if current input has something
        //  yes -> add a new todo
        // find todo with actionId, remove it from the list, place text in input area, request focus

        final long id = editTodoAction.id();

        final InputState inputState = state.get(InputState.class).get();
        final TodosState todosState = state.get(TodosState.class).get();

        final List<Todo> list = todosState.todos();

        final int size = CollectionUtils.isEmpty(list)
                ? 0
                : list.size();

        if (size == 0) {
            // unexpected state really
            // do nothing
            return;
        }

        final List<Todo> outList = new ArrayList<>(size);

        Todo todo;
        Todo selected = null;

        for (int i = 0; i < size; i++) {
            todo = list.get(i);
            if (id == todo.id()) {
                selected = todo;
            } else {
                outList.add(todo);
            }
        }

        if (selected == null) {
            // it's weird
            return;
        }

        final Todo editTodo = selected;

        final TodosState outTodosState = new TodosState();

        if (!TextUtils.isEmpty(inputState.currentInput())) {
            // we should add a new todo
            // and here is the real todo: here should not be any model logic
            // this should do a standalone module (add/remove,etc)
            outList.add(new Todo(System.currentTimeMillis(), inputState.currentInput(), false));
            outTodosState.scrollToLast(true);
        }

        outTodosState.todos(outList);

        final InputState outInputState = inputState.clone(in -> {
            final String name = editTodo.getName();
            in.currentInput(name);
            in.hasFocus(true);
            in.selectionStart(name.length());
            in.selectionEnd(name.length());
        });

        state.set(outTodosState);
        state.set(outInputState);
    }
}
