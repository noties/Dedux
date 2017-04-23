package ru.noties.todo.state;

import javax.annotation.Nonnull;

public interface Apply<T> {
    void apply(@Nonnull T t);
}
