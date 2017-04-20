package ru.noties.tddd.app.components.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import ru.noties.tddd.sample.R;
import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.app.core.IconView;
import ru.noties.tddd.app.model.ClearDoneAction;
import ru.noties.tddd.app.model.ToggleAllDoneAction;
import ru.noties.tddd.utils.ViewUtils;

public class AppBarComponent extends LinearLayout {

    private ComponentHelper helper;

    private TextView title;
    private IconView filter;
    private IconView action;
    private View clear;

    public AppBarComponent(Context context) {
        super(context);
        init(context, null);
    }

    public AppBarComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        helper = ComponentHelper.install(context);
        if (helper == null) {
            if (!isInEditMode()) {
                throw new IllegalStateException("Cannot install ComponentHelper");
            }
        }

        setOrientation(VERTICAL);

        inflate(context, R.layout.view_app_bar, this);

        this.title = ViewUtils.findView(this, R.id.app_bar_title);
        this.filter = ViewUtils.findView(this, R.id.app_bar_filter);
        this.action = ViewUtils.findView(this, R.id.app_bar_action);
        this.clear = findViewById(R.id.app_bar_clear);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (helper != null) {
            helper.attach(AppBarState.class, this::render);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (helper != null) {
            helper.detach();
        }
    }

    private void render(@Nullable AppBarState state) {
        renderTitle(state);
        renderFilter(state);
        renderClear(state);
        renderAllDone(state);
    }

    private void renderTitle(@Nullable AppBarState state) {

        final CharSequence cs = state != null
                ? state.title()
                : null;

        title.setText(cs);
    }

    private void renderFilter(@Nullable AppBarState state) {
        final int colorRes = state != null && state.filterEnabled()
                ? R.color.colorAccent
                : R.color.colorWhite;
        filter.setColorRes(colorRes);
        filter.setOnClickListener(v -> helper.store().dispatch(null));
    }

    private void renderClear(@Nullable AppBarState state) {
        final boolean visible = state != null && state.clearEnabled();

        ViewUtils.setVisible(clear, visible, INVISIBLE);

        final OnClickListener listener;
        if (visible) {
            listener = v -> helper.store().dispatch(new ClearDoneAction());
        } else {
            listener = null;
        }
        clear.setOnClickListener(listener);
    }

    private void renderAllDone(@Nullable AppBarState state) {

        final boolean enabled = state != null && state.toggleDoneEnabled();
        ViewUtils.setVisible(action, enabled, INVISIBLE);

        final boolean allDone = enabled && state.allDone();

        action.setActivated(allDone);
        action.setEnabled(enabled);

        final View.OnClickListener onClickListener = v -> helper.store().dispatch(new ToggleAllDoneAction());
        action.setOnClickListener(onClickListener);
    }
}
