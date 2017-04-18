package dedux;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Consumer<T> {
    void apply(@Nonnull Subscription subscription, @Nullable T t);
}
