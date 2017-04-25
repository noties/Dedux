package dedux.builders;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Middleware;
import dedux.Reducer;

public interface StoreModule<A extends Action> {

    @Nonnull
    Reducer<A> reducer();

    @Nullable
    Middleware<A> middleware();
}
