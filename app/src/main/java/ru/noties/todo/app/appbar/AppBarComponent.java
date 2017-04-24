package ru.noties.todo.app.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.app.navigation.confirm.ConfirmClearDoneAction;
import ru.noties.todo.app.navigation.core.NavigationOpenAccountAction;
import ru.noties.todo.app.todo.core.ToggleAllDoneAction;
import ru.noties.todo.core.IconView;
import ru.noties.todo.sample.R;
import ru.noties.todo.utils.ViewUtils;

public class AppBarComponent extends LinearLayout {

    private ComponentHelper helper;

    private TextView title;
    private IconView filter;
    private IconView action;
    private View clear;
    private TextView countDone;

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
        this.filter = ViewUtils.findView(this, R.id.app_bar_account);
        this.action = ViewUtils.findView(this, R.id.app_bar_action);
        this.clear = findViewById(R.id.app_bar_clear);
        this.countDone = ViewUtils.findView(this, R.id.app_bar_done_count);
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

    private void render(@Nonnull AppBarState state) {
        renderTitle(state);
        renderAccount(state);
        renderClear(state);
        renderAllDone(state);
        renderCountDone(state);
    }

    private void renderTitle(@Nonnull AppBarState state) {
        title.setText(state.title());
    }

    private void renderAccount(@Nonnull AppBarState state) {
        final int colorRes = state.loggedIn()
                ? R.color.colorAccent
                : R.color.colorWhite;
        filter.setColorRes(colorRes);
        filter.setOnClickListener(v -> helper.store().dispatch(new NavigationOpenAccountAction()));
    }

    private void renderClear(@Nonnull AppBarState state) {

        final boolean visible = state.clearEnabled();

        ViewUtils.setVisible(clear, visible, INVISIBLE);

        final OnClickListener listener;
        if (visible) {
            listener = v -> helper.store().dispatch(new ConfirmClearDoneAction());
        } else {
            listener = null;
        }
        clear.setOnClickListener(listener);
    }

    private void renderAllDone(@Nonnull AppBarState state) {

        final boolean enabled = state.toggleDoneEnabled();
        ViewUtils.setVisible(action, enabled, INVISIBLE);

        final boolean allDone = enabled && state.allDone();

        action.setActivated(allDone);
        action.setEnabled(enabled);

        final View.OnClickListener onClickListener = v -> helper.store().dispatch(new ToggleAllDoneAction());
        action.setOnClickListener(onClickListener);

        // todo, initial state on JB emulator device is not rendered (nothing is displayed)
        // though (if) it's clickable, it's clickable and after state is toggled
        // everything is displayed correctly
    }

    private void renderCountDone(@Nonnull AppBarState state) {

        final int done = state.doneCount();

        final boolean visible = done > 0;

        ViewUtils.setVisible(countDone, visible);
        countDone.setText(String.valueOf(done));
    }
}
