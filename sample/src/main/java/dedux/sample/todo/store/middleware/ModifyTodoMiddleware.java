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
    public void apply(@Nonnull Store store, @Nonnull ModifyTodoAction action, @Nonnull Next next) {
        // due to the fact that this middleware (in general) can do some async work,
        // maybe it's better to provide a, for example, `Done` callback?
        // which will be called after action is finally reduced?
        next.next();
        store.dispatch(new AppBarCountDoneAction());
        store.dispatch(new AppBarCheckDoneAction());
    }
}
