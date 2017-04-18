package dedux;

import javax.annotation.Nullable;

public interface MutableOp<T> extends Op<T> {
    void set(@Nullable T t);
}
