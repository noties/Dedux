package ru.noties.tddd.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import ru.noties.tddd.sample.R;

public class AppComponent extends FrameLayout {

    public AppComponent(Context context) {
        super(context);
        init(context, null);
    }

    public AppComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        inflate(context, R.layout.view_app, this);
    }
}
