package ru.noties.todo.app.navigation.core;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class NavigationOpenAccountReducer implements Reducer<NavigationOpenAccountAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull NavigationOpenAccountAction navigationOpenAccountAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showAccount(true));
        state.set(out);
    }
}
