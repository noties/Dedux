package ru.noties.dedux.sample.state;

import javax.annotation.Nonnull;

public interface Apply<T> {
    void apply(@Nonnull T t);
}
