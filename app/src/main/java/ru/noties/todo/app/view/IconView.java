package ru.noties.todo.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import ru.noties.todo.R;

public class IconView extends ImageView {

    private int color;

    public IconView(Context context) {
        super(context);
        init(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            final TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.IconView);
            try {
                setColor(array.getColor(R.styleable.IconView_iv_color, 0));
            } finally {
                array.recycle();
            }
        }
    }

    public void setColorRes(@ColorRes int colorRes) {
        setColor(getColor(getContext(), colorRes));
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        final ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        setColorFilter(filter);
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    private static int getColor(Context context, @ColorRes int colorRes) {
        final int out;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            out = context.getResources().getColor(colorRes, context.getTheme());
        } else {
            out = context.getResources().getColor(colorRes);
        }
        return out;
    }
}
