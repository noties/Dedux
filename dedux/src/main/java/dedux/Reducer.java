package dedux;

import javax.annotation.Nonnull;

public interface Reducer<A extends Action, S extends StateItem> {

    @Nonnull
    S reduce(@Nonnull State state, @Nonnull A a);
}
