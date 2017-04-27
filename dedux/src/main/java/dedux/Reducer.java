package dedux;

import javax.annotation.Nonnull;

public interface Reducer<A extends Action> {

    @Nonnull
    Class<A> actionType();

    void reduce(@Nonnull MutableState state, @Nonnull A a);
}
