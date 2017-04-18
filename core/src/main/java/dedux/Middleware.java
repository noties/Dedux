package dedux;

import javax.annotation.Nonnull;

public interface Middleware<A extends Action> {

    interface Next {
        void next();
    }

    // we can dispatch new action here, and we can modify action that came
    // but we cannot pass to `next` another action
    void apply(@Nonnull Store store, @Nonnull A action, @Nonnull Next next);
}
