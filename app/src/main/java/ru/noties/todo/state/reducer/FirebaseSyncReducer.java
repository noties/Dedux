package ru.noties.todo.state.reducer;

import java.util.List;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import ru.noties.todo.state.action.FirebaseSyncAction;
import ru.noties.todo.utils.CollectionUtils;

public class FirebaseSyncReducer implements Reducer<FirebaseSyncAction> {

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull FirebaseSyncAction firebaseSyncAction) {

        final List<Object> list = firebaseSyncAction.list();
        if (!CollectionUtils.isEmpty(list)) {
            for (Object o: list) {
                state.set(o);
            }
        }
    }
}
