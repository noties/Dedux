package dedux;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MutableState implements State {


    public abstract static class Storage {

        public Storage(@Nullable List<? extends StateItem> initial) {

        }

        @Nullable
        public abstract <S extends StateItem> S get(@Nonnull Class<S> cl);

        public abstract <S extends StateItem> void set(@Nonnull S s);
    }


    public MutableState(@Nonnull Storage storage) {

    }

    @Nonnull
    @Override
    public abstract  <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl);

    // also, need to note, that this must a simple class, not an array,
    // not a generic one of any kind (List<?> etc)

    // helper function == `get(SomeClass.class).set(new SomeClass(123L))`
    // NB, DOES NOT ALLOW NULL, if NULL value must be set use `get(*.class).set(null)`
    public abstract <S extends StateItem> void set(@Nonnull S s);
}
