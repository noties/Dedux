package ru.noties.todo.app.navigation.confirm;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.navigation.core.NavigationState;

public class ConfirmCloseReducer implements Reducer<ConfirmCloseAction> {

    @Nonnull
    @Override
    public Class<ConfirmCloseAction> actionType() {
        return ConfirmCloseAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ConfirmCloseAction confirmCloseAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showConfirm(false));
        state.set(out);
    }
}
