package ru.noties.todo.store.reducer;

import android.content.res.Resources;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.R;
import ru.noties.todo.store.state.AppBarState;
import ru.noties.todo.store.state.ConfirmState;
import ru.noties.todo.store.state.NavigationState;
import ru.noties.todo.store.action.ConfirmClearDoneAction;

public class ConfirmClearDoneReducer implements Reducer<ConfirmClearDoneAction> {

    private final Resources resources;

    public ConfirmClearDoneReducer(@Nonnull Resources resources) {
        this.resources = resources;
    }

    @Nonnull
    @Override
    public Class<ConfirmClearDoneAction> actionType() {
        return ConfirmClearDoneAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ConfirmClearDoneAction confirmClearDoneAction) {

        final AppBarState appBarState = state.get(AppBarState.class).get();
        final int done = appBarState.doneCount();

        final String message = resources.getString(
                R.string.confirm_clear_done_message,
                resources.getQuantityString(R.plurals.todos, done, done)
        );

        final ConfirmState confirmState = new ConfirmState()
                .title(resources.getString(R.string.confirm_clear_done_title))
                .message(message)
                .confirmText(resources.getString(R.string.confirm_clear_done_confirm))
                .cancelText(resources.getString(R.string.confirm_clear_done_cancel));
        state.set(confirmState);

        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showConfirm(true));
        state.set(out);
    }
}
