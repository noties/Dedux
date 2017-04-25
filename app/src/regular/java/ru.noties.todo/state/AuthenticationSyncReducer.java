package ru.noties.todo.state;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;

public class AuthenticationSyncReducer implements Reducer<AuthenticationSyncAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AuthenticationSyncAction authenticationSyncAction) {
        // no op, and we should not be called...like never
    }
}
