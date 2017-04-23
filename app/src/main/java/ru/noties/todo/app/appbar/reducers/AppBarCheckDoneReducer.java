package ru.noties.todo.app.appbar.reducers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.appbar.actions.AppBarCheckDoneAction;
import ru.noties.todo.app.appbar.state.AppBarState;
import ru.noties.todo.app.todo.core.TodosState;
import ru.noties.todo.app.todo.core.Todo;
import ru.noties.todo.utils.CollectionUtils;

public class AppBarCheckDoneReducer implements Reducer<AppBarCheckDoneAction> {

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
    private static DoneFlags doneFlags(@Nullable TodosState state) {
        final DoneFlags out;
        if (state == null) {
            out = new DoneFlags(false, false, false);
        } else {
            final List<Todo> todos = state.todos();
            if (CollectionUtils.isEmpty(todos)) {
                out = new DoneFlags(false, false, false);
            } else {
                boolean allDone = true;
                boolean hasDone = false;
                for (Todo todo: todos) {
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
