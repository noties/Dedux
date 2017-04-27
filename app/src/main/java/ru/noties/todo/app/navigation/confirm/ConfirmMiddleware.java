package ru.noties.todo.app.navigation.confirm;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import ru.noties.todo.app.todo.core.ClearDoneAction;

public class ConfirmMiddleware implements Middleware<ConfirmAction> {

    @Nonnull
    @Override
    public Class<ConfirmAction> actionType() {
        return ConfirmAction.class;
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull ConfirmAction action, @Nonnull Next next) {

        store.dispatch(new ConfirmCloseAction());

        // right now we have only one confirmation -> remove all done
        if (action.confirmed()) {
            store.dispatch(new ClearDoneAction());
        }

        // NB, we do not pass this event further the chain
    }
}
