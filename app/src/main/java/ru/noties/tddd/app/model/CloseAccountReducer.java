package ru.noties.tddd.app.model;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.tddd.app.components.navigation.NavigationState;

public class CloseAccountReducer implements Reducer<CloseAccountAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull CloseAccountAction closeAccountAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showAccount(false));
        state.set(out);
    }
}
