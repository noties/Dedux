package dedux;

import javax.annotation.Nonnull;

public interface Converter<T, R> {
    R apply(@Nonnull T t);
}
