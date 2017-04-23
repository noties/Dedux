package ru.noties.todo.app.model;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.components.navigation.NavigationState;

public class OpenAccountReducer implements Reducer<OpenAccountAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull OpenAccountAction openAccountAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showAccount(true));
        state.set(out);
    }
}
