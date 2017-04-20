package ru.noties.tddd.app.model;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.tddd.app.components.appbar.AppBarState;
import ru.noties.tddd.data.Todo;
import ru.noties.tddd.utils.CollectionUtils;

public class CheckDoneReducer implements Reducer<CheckDoneAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull CheckDoneAction checkDoneAction) {

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
