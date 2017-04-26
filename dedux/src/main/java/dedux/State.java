package dedux;

import java.util.List;

import javax.annotation.Nonnull;

public interface State {

    @Nonnull
    <S extends StateItem> Op<S> get(@Nonnull Class<S> cl);

    @Nonnull
    List<StateItem> state();
}
