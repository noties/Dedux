package dedux;

import javax.annotation.Nonnull;

public interface MutableState extends State {

    @Override
    <R> MutableOp<R> get(@Nonnull Class<R> cl);

    // helper function == `get(SomeClass.class).set(new SomeClass(123L))`
    // NB, DOES NOT ALLOW NULL, if NULL value must be set use `get(*.class).set(null)`
    <T> void set(@Nonnull T t);

    Subscription subscribe(@Nonnull Consumer<MutableState> consumer);
}
