package dedux.sample.todo.store.reducer;

import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.model.Todo;
import dedux.sample.todo.store.state.AppBarState;
import dedux.sample.todo.store.state.TodosState;
import dedux.sample.todo.store.action.AppBarCheckDoneAction;
import dedux.sample.todo.utils.CollectionUtils;

public class AppBarCheckDoneReducer implements Reducer<AppBarCheckDoneAction> {

    @Nonnull
    @Override
    public Class<AppBarCheckDoneAction> actionType() {
        return AppBarCheckDoneAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AppBarCheckDoneAction appBarCheckDoneAction) {

        final TodosState todosState = state.get(TodosState.class).get();

        final DoneFlags doneFlags = doneFlags(todosState);

        final AppBarState appBarState = state.get(AppBarState.class).get();

        if (appBarState.allDone() != doneFlags.allDone
                || appBarState.clearEnabled() != doneFlags.hasDone
                || appBarState.toggleDoneEnabled() != doneFlags.allDoneEnabled) {
            final AppBarState out = appBarState.clone((in) -> {
                in.allDone(doneFlags.allDone)
                        .clearEnabled(doneFlags.hasDone)
                        .toggleDoneEnabled(doneFlags.allDoneEnabled);
            });
            state.set(out);
        }
    }

    @Nonnull
    private static DoneFlags doneFlags(@Nonnull TodosState state) {
        final DoneFlags out;
        final List<Todo> todos = state.todos();
        if (CollectionUtils.isEmpty(todos)) {
            out = new DoneFlags(false, false, false);
        } else {
            boolean allDone = true;
            boolean hasDone = false;
            for (Todo todo : todos) {
                if (!todo.isDone()) {
                    allDone = false;
                    if (hasDone) {
                        break;
                    }
                } else {
                    hasDone = true;
                }
            }
            out = new DoneFlags(hasDone, allDone, true);
        }
        return out;
    }

    private static class DoneFlags {

        final boolean hasDone;
        final boolean allDone;
        final boolean allDoneEnabled;

        DoneFlags(boolean hasDone, boolean allDone, boolean allDoneEnabled) {
            this.hasDone = hasDone;
            this.allDone = allDone;
            this.allDoneEnabled = allDoneEnabled;
        }
    }
}
