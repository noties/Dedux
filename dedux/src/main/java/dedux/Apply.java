package dedux;

import javax.annotation.Nonnull;

public interface Apply<T> {
    void apply(@Nonnull T t);
}
