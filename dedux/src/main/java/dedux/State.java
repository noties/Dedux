package dedux;

import javax.annotation.Nonnull;

public interface State {

    @Nonnull
    <S extends StateItem> Op<S> get(@Nonnull Class<S> cl);
}
