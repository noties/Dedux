package dedux;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An extension over {@link State} that allows to _mutate_ underlying state objects by
 * returning a {@link MutableOp} for a state class
 *
 * @see #get(Class)
 * @see State
 * @see StateItem
 * @since 1.0.0
 */
public abstract class MutableState implements State {


    /**
     * Base class that holds the {@link StateItem} associated with {@link MutableState}.
     * Should be as simple as possible. Delegates creation of missing items to holding {@link MutableState}
     *
     * @see MemoryStorage
     * @since 1.0.0
     */
    public abstract static class Storage {

        /**
         * Constructor that ensures that a sibling receives a List of initial items
         *
         * @param initial nullable List of initial items
         * @since 1.0.0
         */
        @SuppressWarnings("unused")
        public Storage(@Nullable List<? extends StateItem> initial) {

        }

        /**
         * @param cl  Class of requested {@link StateItem}
         * @param <S> a type that is a subtype of {@link StateItem}
         * @return a value stored in this storage or null, if there is no item of supplied type
         * @since 1.0.0
         */
        @Nullable
        public abstract <S extends StateItem> S get(@Nonnull Class<S> cl);

        /**
         * @param s   an instance of {@link StateItem} to be stored
         * @param <S> a subtype of {@link StateItem}
         * @since 1.0.0
         */
        public abstract <S extends StateItem> void set(@Nonnull S s);
    }

    /**
     * Constructor that ensures that a sibling receives a {@link Storage} object, as
     * {@link MutableState} delegates actual storing to it
     *
     * @param storage non-null {@link Storage} instance
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public MutableState(@Nonnull Storage storage) {

    }

    /**
     * Overrides the {@link Op#get()} method to return {@link MutableOp} instead of {@link Op}
     *
     * @param cl  type of the requested {@link StateItem}
     * @param <S> a subtype of {@link StateItem}
     * @return a {@link MutableOp}
     * @see MutableOp
     * @since 1.0.0
     */
    @Nonnull
    @Override
    public abstract <S extends StateItem> MutableOp<S> get(@Nonnull Class<S> cl);

    /**
     * A convenience method for calling `get(SomeClass.class).set(new SomeClass())`
     *
     * @param s   value to be set
     * @param <S> a subclass of type {@link StateItem}
     * @see #get(Class)
     * @see MutableOp#set(Object)
     * @since 1.0.0
     */
    public abstract <S extends StateItem> void set(@Nonnull S s);
}
