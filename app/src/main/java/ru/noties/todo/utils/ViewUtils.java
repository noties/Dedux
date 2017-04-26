package ru.noties.todo.utils;

import android.support.annotation.IntDef;
import android.view.View;

import javax.annotation.Nonnull;

public class ViewUtils {

    @IntDef({View.INVISIBLE, View.GONE})
    @interface NotVisible {}

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
