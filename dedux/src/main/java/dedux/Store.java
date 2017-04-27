package dedux;

import javax.annotation.Nonnull;

public interface Store {

    @Nonnull
    State state();

    void dispatch(@Nonnull Action action);
}
