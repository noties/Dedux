package ru.noties.tddd.app.model;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.tddd.app.components.navigation.NavigationState;

public class CloseConfirmReducer implements Reducer<CloseConfirmAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull CloseConfirmAction closeConfirmAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showConfirm(false));
        state.set(out);
    }
}
