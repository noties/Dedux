package dedux;

import javax.annotation.Nonnull;

public interface Reducer<A extends Action> {
    void reduce(@Nonnull MutableState state, @Nonnull A a);
}
