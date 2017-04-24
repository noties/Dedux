package dedux;

import java.util.Map;

import javax.annotation.Nonnull;

public interface State {

    @Nonnull
    <S extends StateItem> Op<S> get(@Nonnull Class<S> cl);

    @Nonnull
    Map<Class<? extends StateItem>, StateItem> state();
}
