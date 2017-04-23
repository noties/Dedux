package ru.noties.todo.app.model;

import android.content.res.Resources;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.components.CountDoneState;
import ru.noties.todo.app.components.confirm.ConfirmState;
import ru.noties.todo.app.components.navigation.NavigationState;
import ru.noties.todo.sample.R;

public class ConfirmClearDoneReducer implements Reducer<ConfirmClearDoneAction> {

    private final Resources resources;

    public ConfirmClearDoneReducer(@Nonnull Resources resources) {
        this.resources = resources;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ConfirmClearDoneAction confirmClearDoneAction) {

        final CountDoneState countDoneState = state.get(CountDoneState.class).get();
        final int done = countDoneState != null
                ? countDoneState.count()
                : 0; // weird, should not happen.. ever

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
