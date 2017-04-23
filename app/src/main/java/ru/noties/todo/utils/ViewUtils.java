package ru.noties.todo.utils;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.view.View;

import javax.annotation.Nonnull;

public class ViewUtils {

    @IntDef({View.INVISIBLE, View.GONE})
    @interface NotVisible {}

    public static <V extends View> V findView(@Nonnull Activity activity, @IdRes int id) {
        //noinspection unchecked
        return (V) activity.findViewById(id);
    }

    public static <V extends View> V findView(@Nonnull View view, @IdRes int id) {
        //noinspection unchecked
        return (V) view.findViewById(id);
    }

    public static <V extends View> void setVisible(@Nonnull View view, boolean visible) {
        setVisible(view, visible, View.GONE);
    }

    public static <V extends View> void setVisible(
            @Nonnull View view,
            boolean visible,
            @NotVisible int notVisible
    ) {
        final int visibility = visible
                ? View.VISIBLE
                : notVisible;
        view.setVisibility(visibility);
    }

    private ViewUtils() {
    }
}
