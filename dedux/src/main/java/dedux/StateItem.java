package dedux;

import javax.annotation.Nonnull;

import dedux.internal.ReflectUtils;

/**
 * This is an interface that must be implemented in state objects. For a simpler usage the
 * {@link StateItemBase} can be used, as it's already implements the `clone` methods.
 * <p>
 * This interface defines 2 `clone` methods. First one can be used when type of a stateItem
 * can be understood by the compiler, for example:
 * {@code
 * final Impl impl = some.clone(in -> {}); // `in` argument of type Impl
 * final Some some = impl.clone(in -> {}); // `in` argument will be Some (no matter that we have called `clone` on `impl`)
 * }
 * <p>
 * In order to help compiler understand the type, the second clone method can be used:
 * {@code
 * final Some some = impl.clone(Impl.class, in -> {}); // `in` arguments of type Impl
 * }
 * <p>
 * Please note, that if you are implementing this interface manually, the class should have
 * an empty public constructor (as reflection is used to instantiate the object)
 *
 * @see #clone(Apply)
 * @see #clone(Class, Apply)
 * @see Cloner
 * @see StateItemBase
 * @since 1.0.0
 */
public interface StateItem {

    /**
     * @param apply a {@link Apply} instance to process cloned object
     * @param <S>   subtype of {@link StateItem}
     * @return new instance of this class (shallow copy)
     * @see #clone(Class, Apply)
     * @see Cloner
     * @see Apply
     * @since 1.0.0
     */
    @Nonnull
    <S extends StateItem> S clone(@Nonnull Apply<? super S> apply);

    /**
     * @param cl    specific type
     * @param apply a {@link Apply} instance to process cloned object
     * @param <S>   subtype of {@link StateItem}
     * @return new instance of this class (shallow copy)
     * @see Cloner
     * @see Apply
     * @since 1.0.0
     */
    @Nonnull
    <S extends StateItem> S clone(@Nonnull Class<S> cl, @Nonnull Apply<? super S> apply);


    /**
     * Helper class to be used to implement `clone` methods in {@link StateItem} sub-types
     *
     * @see StateItemBase#clone(Class, Apply)
     * @see StateItemBase#clone(Apply)
     * @since 1.0.0
     */
    @SuppressWarnings("WeakerAccess")
    class Cloner {

        @Nonnull
        public static <S extends StateItem> S clone(S who, @Nonnull Apply<? super S> apply) {
            final S s = ReflectUtils.newInstance(who.getClass());
            ReflectUtils.copy(who, s);
            apply.apply(s);
            return s;
        }

        private Cloner() {
        }
    }
}
