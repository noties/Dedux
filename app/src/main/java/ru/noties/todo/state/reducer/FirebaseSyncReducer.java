package ru.noties.todo.state.reducer;

import java.util.Map;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.state.action.FirebaseSyncAction;

public class FirebaseSyncReducer implements Reducer<FirebaseSyncAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull FirebaseSyncAction firebaseSyncAction) {

        final Map<String, Object> map = firebaseSyncAction.map();
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            state.set(entry.getKey(), entry.getValue());
        }
    }
}
