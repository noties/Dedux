package ru.noties.dedux.sample.app.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import ru.noties.dedux.sample.R;

public class IconView extends ImageView {

    private int color;

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        setColor(getResources().getColor(colorRes));
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
}
