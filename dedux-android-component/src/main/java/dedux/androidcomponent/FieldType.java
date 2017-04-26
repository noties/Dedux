package dedux.androidcomponent;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

enum FieldType {

    BYTE
    , BOOLEAN
    , SHORT
    , INT
    , LONG
    , FLOAT
    , DOUBLE
    , STRING
    , NOT_SUPPORTED
    ;

    @Nonnull
    public static FieldType parseType(Field field) {

        final FieldType type;

        final Class<?> cl = field.getType();

        if (Byte.class.equals(cl) || Byte.TYPE.equals(cl)) {
            type = FieldType.BYTE;

        } else if (Boolean.class.equals(cl) || Boolean.TYPE.equals(cl)) {
            type = FieldType.BOOLEAN;

        } else if (Short.class.equals(cl) || Short.TYPE.equals(cl)) {
            type = FieldType.SHORT;

        } else if (Integer.class.equals(cl) || Integer.TYPE.equals(cl)) {
            type = FieldType.INT;

        } else if (Long.class.equals(cl) || Long.TYPE.equals(cl)) {
            type = FieldType.LONG;

        } else if (Float.class.equals(cl) || Float.TYPE.equals(cl)) {
            type = FieldType.FLOAT;

        } else if (Double.class.equals(cl) || Double.TYPE.equals(cl)) {
            type = FieldType.DOUBLE;

        } else if (String.class.equals(cl)) {
            type = FieldType.STRING;

        } else {
            type = FieldType.NOT_SUPPORTED;
        }

        return type;
    }
}
