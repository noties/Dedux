package dedux;

import javax.annotation.Nonnull;

public interface StateItem {

    // we need to define 2 methods for cloning
    // first can be used when type of return value can be reduced by compiler. for example
    // when declaring local variable or returning from a method with specific type:
    //  `final Impl impl = some.clone(in -> {});` // `in` is of type `Impl`
    //
    //  ```
    //      Impl doSomething(Impl impl) {
    //          return impl.clone(in -> {}); // `in` here of type `Impl`
    //      }
    //  }```
    //
    // But if a method, for example, returns some base class and `clone` should be called
    // on implementation instead of a base class, the second `clone` method can be used:
    //  ```
    //      StateItem doSomething(Impl impl) {
    //          return impl.clone(Impl.class, in -> {}); // `in` here of type `Impl`
    //      }
    //  ```
    //
    // so, 2 methods are added for convenience, so we don't have to override `clone` method
    // in all of siblings of `StateItem`
    
    @Nonnull
    <S extends StateItem> S clone(@Nonnull Apply<? super S > apply);

    @Nonnull
    <S extends StateItem> S clone(@Nonnull Class<S> cl, @Nonnull Apply<? super S> apply);
}
