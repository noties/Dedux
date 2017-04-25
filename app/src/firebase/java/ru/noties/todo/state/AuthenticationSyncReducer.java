package ru.noties.todo.state;

import java.util.Map;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.StateItem;

public class AuthenticationSyncReducer implements Reducer<AuthenticationSyncAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull AuthenticationSyncAction firebaseSyncAction) {

        final Map<Class<? extends StateItem>, StateItem> map = firebaseSyncAction.map();
        for (Map.Entry<Class<? extends StateItem>, StateItem> entry : map.entrySet()) {
            state.set(entry.getValue());
        }
    }
}
