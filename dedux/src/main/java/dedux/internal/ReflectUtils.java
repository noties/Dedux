package dedux.internal;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

public class ReflectUtils {

    public static <T> T newInstance(@Nonnull Class<?> cl) {
        try {
            //noinspection unchecked
            return (T) cl.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static <T> void copy(@Nonnull T from, @Nonnull T to) {
        final Field[] fields = from.getClass().getDeclaredFields();
        if (fields != null
                && fields.length > 0) {
            for (Field field: fields) {
                field.setAccessible(true);
                try {
                    field.set(to, field.get(from));
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }
    }

    private ReflectUtils() {}
}
