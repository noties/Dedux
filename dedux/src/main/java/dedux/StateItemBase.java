package dedux;

import javax.annotation.Nonnull;

import dedux.internal.ReflectUtils;

// Subclasses must follow Java Bean convention and have an empty constructor
public class StateItemBase implements StateItem {

    @Nonnull
    @Override
    public <S extends StateItem> S clone(@Nonnull Apply<S> apply) {
        final S s = ReflectUtils.newInstance(getClass());
        ReflectUtils.copy(this, s);
        apply.apply(s);
        return s;
    }
}
