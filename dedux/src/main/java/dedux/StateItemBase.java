package dedux;

import javax.annotation.Nonnull;

import dedux.internal.ReflectUtils;

// Subclasses must follow Java Bean convention and have an empty constructor
// default values can be set in default empty constructor or directly into fields
public class StateItemBase implements StateItem {

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Apply<? super S> apply) {
        final S s = ReflectUtils.newInstance(getClass());
        ReflectUtils.copy(this, s);
        apply.apply(s);
        return s;
    }

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Class<S> cl, @Nonnull Apply<? super S> apply) {
        final S s = ReflectUtils.newInstance(cl);
        ReflectUtils.copy(this, s);
        apply.apply(s);
        return s;
    }
}
