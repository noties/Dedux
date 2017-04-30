package dedux.sample.todo.store.middleware;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import dedux.sample.todo.store.action.ClearDoneAction;
import dedux.sample.todo.store.action.ConfirmAction;
import dedux.sample.todo.store.action.ConfirmCloseAction;

public class ConfirmMiddleware implements Middleware<ConfirmAction> {

    @Nonnull
    @Override
    public Class<ConfirmAction> actionType() {
        return ConfirmAction.class;
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull ConfirmAction action, @Nonnull ActionHandler<ConfirmAction> handler) {

        store.dispatch(new ConfirmCloseAction());

        // right now we have only one confirmation -> remove all done
        if (action.confirmed()) {
            store.dispatch(new ClearDoneAction());
        }


        // NB, we do not pass this event further the chain
        handler.cancelActionDispatch();
    }
}
