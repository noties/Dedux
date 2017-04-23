package ru.noties.todo.app.model;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.app.components.navigation.NavigationState;

public class BackPressedReducer implements Reducer<BackPressedAction> {
    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull BackPressedAction backPressedAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out;
        if (navigationState.showAccount()) {
            out = navigationState.clone(in -> in.showAccount(false));
        } else {

        }
    }
}
