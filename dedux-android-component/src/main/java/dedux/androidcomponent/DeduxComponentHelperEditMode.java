package dedux.androidcomponent;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Action;
import dedux.Consumer;
import dedux.Op;
import dedux.State;
import dedux.StateItem;
import dedux.Store;
import dedux.Subscription;
import dedux.internal.ReflectUtils;
import dedux.internal.SubscriptionNoOp;

class DeduxComponentHelperEditMode implements DeduxComponentHelper {

    static DeduxComponentHelper install(@Nonnull Context context, @Nullable AttributeSet attributeSet) {
        return new DeduxComponentHelperEditMode(context, attributeSet);
    }

    private final Store store;
    private final Map<String, String> map;

    private DeduxComponentHelperEditMode(@Nonnull Context context, @Nullable AttributeSet attributeSet) {
        this.store = new StoreNoOp();
        // we need to build own copy of attributes, because (seems so) attributeSet is cleared
        // after this method exists
        this.map = EditModeHelper.buildAttributes(context, attributeSet);
    }

    @Override
    public Store store() {
        return this.store;
    }

    @Override
    public <T extends StateItem> void subscribeTo(@Nonnull Class<T> state, @Nonnull DeduxComponent.OnStateListener<? super T> listener) {
        listener.apply(create(state, map));
    }

    @Override
    public void unsubscribe() {
        // no op
    }

    private static <T extends StateItem> T create(Class<T> cl, Map<String, String> map) {
        final T t = ReflectUtils.newInstance(cl);
        applyAttributes(t, map);
        return t;
    }

    private static void applyAttributes(Object o, Map<String, String> map) {

        final Field[] fields = o.getClass().getDeclaredFields();
        if (fields == null
                || fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            applyField(field, o, map);
        }
    }

    private static void applyField(Field field, Object who, Map<String, String> map) {

        // first of all let's check if attributeSet contains our name
        final String name = field.getName();
        final String value = map.get(name);
        if (TextUtils.isEmpty(value)) {
            // no, it's not present here
            return;
        }

        // next, we need to apply it
        final FieldType type = FieldType.parseType(field);

        switch (type) {

            case BYTE:
                set(field, who, Byte.parseByte(value));
                break;

            case BOOLEAN:
                set(field, who, Boolean.parseBoolean(value));
                break;

            case SHORT:
                set(field, who, Short.parseShort(value));
                break;

            case INT:
                set(field, who, Integer.parseInt(value));
                break;

            case LONG:
                set(field, who, Long.parseLong(value));
                break;

            case FLOAT:
                set(field, who, Float.parseFloat(value));
                break;

            case DOUBLE:
                set(field, who, Double.parseDouble(value));
                break;

            case STRING:
                set(field, who, EditModeHelper.unescape(value));
                break;

            case NOT_SUPPORTED:
                break;
        }
    }

    private static void set(Field field, Object who, Object value) {
        try {
            field.set(who, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class StoreNoOp implements Store {

        private final State state = new StateNoOp();

        @Nonnull
        @Override
        public State state() {
            return state;
        }

        @Override
        public void dispatch(@Nonnull Action action) {
            // no op
        }

        @Nonnull
        @Override
        public Subscription subscribe(@Nonnull Consumer<State> consumer) {
            return new SubscriptionNoOp();
        }
    }

    private static class StateNoOp implements State {

        @Nonnull
        @Override
        public <S extends StateItem> Op<S> get(@Nonnull Class<S> cl) {
            return ReflectUtils.newInstance(cl);
        }

        @Nonnull
        @Override
        public List<StateItem> state() {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }
    }
}
