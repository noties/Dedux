package dedux;

import java.util.Map;

import javax.annotation.Nonnull;

public interface State {

    <R> Op<R> get(@Nonnull Class<R> cl);

    @Nonnull
    Map<String, Object> state();
}
