package dedux.sample.todo.store.middleware;

import javax.annotation.Nonnull;

import dedux.Middleware;
import dedux.Store;
import dedux.sample.todo.store.action.AppBarCheckDoneAction;
import dedux.sample.todo.store.action.AppBarCountDoneAction;
import dedux.sample.todo.store.action.ModifyTodoAction;

public class ModifyTodoMiddleware implements Middleware<ModifyTodoAction> {

    @Nonnull
    @Override
    public Class<ModifyTodoAction> actionType() {
        return ModifyTodoAction.class;
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull ModifyTodoAction action, @Nonnull ActionHandler<ModifyTodoAction> handler) {
        // no need for explicit `next`, after this method exits, action is passed further
        handler.doOnActionReduced(((s, a) -> {
            s.dispatch(new AppBarCountDoneAction());
            s.dispatch(new AppBarCheckDoneAction());
        }));
    }
}
