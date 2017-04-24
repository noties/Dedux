package dedux;

import javax.annotation.Nonnull;

public interface Op<T> {

    @Nonnull
    T get();

    Subscription subscribe(@Nonnull Consumer<T> consumer);
    Subscription subscribe(boolean deliverFirst, @Nonnull Consumer<T> consumer);

    <R> R to(@Nonnull Converter<Op<T>, R> converter);
}
