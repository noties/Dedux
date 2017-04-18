package dedux;

public interface Converter<T, R> {
    R apply(T t);
}
