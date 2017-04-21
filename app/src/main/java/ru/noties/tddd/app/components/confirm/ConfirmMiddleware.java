package ru.noties.tddd.app.components.confirm;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import ru.noties.tddd.app.model.ClearDoneAction;
import ru.noties.tddd.app.model.CloseConfirmAction;

public class ConfirmMiddleware implements Middleware<ConfirmAction> {
    @Override
    public void apply(@Nonnull Store store, @Nonnull ConfirmAction action, @Nonnull Next next) {

        store.dispatch(new CloseConfirmAction());

        // right now we have only one confirmation -> remove all done
        if (action.confirmed()) {
            store.dispatch(new ClearDoneAction());
        }

        // NB, we do not pass this event further the chain
    }
}
