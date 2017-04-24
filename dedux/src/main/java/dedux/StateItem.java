package dedux;

import javax.annotation.Nonnull;

public interface StateItem {

    @Nonnull
    <S extends StateItem> S clone(@Nonnull Apply<S> apply);
}
