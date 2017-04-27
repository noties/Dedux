package dedux.sample.todo.store.reducer;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.store.state.ListScrollState;
import dedux.sample.todo.store.action.ScrollAction;

public class ScrollReducer implements Reducer<ScrollAction> {

    @Nonnull
    @Override
    public Class<ScrollAction> actionType() {
        return ScrollAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull ScrollAction scrollAction) {
        final ListScrollState scrollState = new ListScrollState()
                .position(scrollAction.position())
                .offset(scrollAction.offset());
        state.set(scrollState);
    }
}
