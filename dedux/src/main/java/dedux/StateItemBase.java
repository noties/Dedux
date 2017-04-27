package dedux;

import javax.annotation.Nonnull;

// Subclasses must follow Java Bean convention and have an empty constructor
// default values can be set in default empty constructor or directly into fields
public class StateItemBase implements StateItem {

    public StateItemBase() {

    }

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Apply<? super S> apply) {
        //noinspection unchecked
        return Cloner.clone((S) this, apply);
    }

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Class<S> cl, @Nonnull Apply<? super S> apply) {
        //noinspection unchecked
        return Cloner.clone((S) this, apply);
    }
}
