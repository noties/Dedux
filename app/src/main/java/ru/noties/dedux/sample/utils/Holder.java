package ru.noties.dedux.sample.utils;

import android.support.annotation.IdRes;
import android.view.View;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Holder {

    public static <H extends Holder> boolean matches(@Nullable H holder, @Nonnull View view) {
        final boolean out;
        if (holder == null
                || holder.view != view) {
            out = false;
        } else {
            out = true;
        }
        return out;
    }

    public final View view;

    public Holder(@Nonnull View view) {
        this.view = view;
    }

    protected <V extends View> V findView(@IdRes int id) {
        return ViewUtils.findView(view, id);
    }
}
