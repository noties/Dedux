package dedux.sample.todo.store.reducer;

import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.AppBarState;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.AppBarCountDoneAction;
import dedux.sample.todo.utils.CollectionUtils;

public class AppBarCountDoneReducer implements Reducer<AppBarCountDoneAction> {

    @Nonnull
    @Override
    public Class<AppBarCountDoneAction> actionType() {
        return AppBarCountDoneAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AppBarCountDoneAction appBarCountDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();
        final AppBarState appBarState = state.get(AppBarState.class).get();

        final int done = count(todosState);
        if (done != appBarState.doneCount()) {
            final AppBarState out = appBarState.clone(in -> in.doneCount(done));
            state.set(out);
        }
    }

    private static int count(@Nonnull TodosState state) {
        final int out;
        if (CollectionUtils.isEmpty(state.todos())) {
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
