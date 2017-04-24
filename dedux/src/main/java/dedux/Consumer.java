package dedux;

import javax.annotation.Nonnull;

public interface Consumer<T> {
    void apply(@Nonnull Subscription subscription, @Nonnull T t);
}
