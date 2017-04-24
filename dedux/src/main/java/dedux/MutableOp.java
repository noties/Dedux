package dedux;

import javax.annotation.Nonnull;

public interface MutableOp<T> extends Op<T> {
    void set(@Nonnull T t);
}
