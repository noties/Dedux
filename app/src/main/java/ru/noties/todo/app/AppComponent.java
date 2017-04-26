package ru.noties.todo.app;

import android.content.Context;
import android.util.AttributeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import ru.noties.todo.R;

public class AppComponent extends DeduxComponent {

    public AppComponent(Context context) {
        super(context);
    }

    public AppComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.view_app, this);

    }

    @Override
    protected void onAttached() {

    }
}
