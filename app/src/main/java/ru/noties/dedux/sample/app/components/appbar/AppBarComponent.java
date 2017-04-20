package ru.noties.dedux.sample.app.components.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import ru.noties.dedux.sample.R;
import ru.noties.dedux.sample.app.components.ComponentHelper;
import ru.noties.dedux.sample.app.core.IconView;
import ru.noties.dedux.sample.utils.ViewUtils;

public class AppBarComponent extends LinearLayout {

    private ComponentHelper helper;

    private TextView title;
    private IconView filter;
    private IconView action;

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
        renderAction(state);
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

    private void renderAction(@Nullable AppBarState state) {

        final AppBarState.ActionState actionState = state != null
                ? state.actionState()
                : null;

        final boolean invisible = actionState == null
                || AppBarState.ActionState.INVISIBLE == actionState;

        ViewUtils.setVisible(action, !invisible);

        action.setActivated(AppBarState.ActionState.CLEAR == actionState);

        final View.OnClickListener onClickListener;
        if (!invisible) {
            onClickListener = v -> helper.store().dispatch(null);
        } else {
            onClickListener = null;
        }
        action.setOnClickListener(onClickListener);
    }
}
