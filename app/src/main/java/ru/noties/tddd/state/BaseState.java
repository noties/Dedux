package ru.noties.tddd.state;

import javax.annotation.Nonnull;

public abstract class BaseState {

    @Nonnull
    public <T extends BaseState> T clone(@Nonnull Apply<T> apply) {
        final T t = ReflectUtils.newInstance(getClass());
        // copy current fields
        ReflectUtils.copy(this, t);
        apply.apply(t);
        return t;
    }
}
