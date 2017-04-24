package dedux;

import javax.annotation.Nonnull;

public interface MutableState extends State {

    @Nonnull
    @Override
    <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl);

    // also, need to note, that this must a simple class, not an array,
    // not a generic one of any kind (List<?> etc)

    // helper function == `get(SomeClass.class).set(new SomeClass(123L))`
    // NB, DOES NOT ALLOW NULL, if NULL value must be set use `get(*.class).set(null)`
    <S extends StateItem> void set(@Nonnull S s);

//    // allows NULL as values
//    <S extends StateItem> void set(@Nonnull Class<S> cl, @Nullable S value);
//
//
    @Nonnull
    Subscription subscribe(@Nonnull Consumer<MutableState> consumer);
}
