package dedux;

import javax.annotation.Nonnull;

public interface Apply<T> {
    T apply(@Nonnull T t);
}
