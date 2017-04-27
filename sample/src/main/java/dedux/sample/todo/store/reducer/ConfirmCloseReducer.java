package dedux.sample.todo.store.reducer;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.store.state.NavigationState;
import dedux.sample.todo.store.action.ConfirmCloseAction;

public class ConfirmCloseReducer implements Reducer<ConfirmCloseAction> {

    @Nonnull
    @Override
    public Class<ConfirmCloseAction> actionType() {
        return ConfirmCloseAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ConfirmCloseAction confirmCloseAction) {
        final NavigationState navigationState = state.get(NavigationState.class).get();
        final NavigationState out = navigationState.clone(in -> in.showConfirm(false));
        state.set(out);
    }
}
