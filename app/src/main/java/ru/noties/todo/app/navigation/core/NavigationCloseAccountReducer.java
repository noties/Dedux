package ru.noties.todo.app.navigation.core;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class NavigationCloseAccountReducer implements Reducer<NavigationCloseAccountAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull NavigationCloseAccountAction navigationCloseAccountAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showAccount(false));
        state.set(out);
    }
}
