package dedux;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MutableState extends State {

    @Override
    <R> MutableOp<R> get(@Nonnull Class<R> cl);

    // also, need to note, that this must a simple class, not an array,
    // not a generic one of any kind (List<?> etc)

    // helper function == `get(SomeClass.class).set(new SomeClass(123L))`
    // NB, DOES NOT ALLOW NULL, if NULL value must be set use `get(*.class).set(null)`
    <T> void set(@Nonnull T t);

    // allows NULL as values
    void set(@Nonnull String className, @Nullable Object value);

    Subscription subscribe(@Nonnull Consumer<MutableState> consumer);
}
