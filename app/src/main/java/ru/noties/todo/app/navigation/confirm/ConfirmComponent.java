package ru.noties.todo.app.navigation.confirm;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.R;
import ru.noties.todo.utils.ViewUtils;

public class ConfirmComponent extends FrameLayout {

    private ComponentHelper helper;

    private TextView title;
    private TextView message;
    private TextView confirm;
    private TextView cancel;

    public ConfirmComponent(Context context) {
        super(context);
        init(context, null);
    }

    public ConfirmComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        helper = ComponentHelper.install(context);
        if (helper == null
                && !isInEditMode()) {
            throw new IllegalStateException();
        }

        inflate(context, R.layout.view_confirm, this);

        this.title = ViewUtils.findView(this, R.id.confirm_title);
        this.message = ViewUtils.findView(this, R.id.confirm_message);
        this.confirm = ViewUtils.findView(this, R.id.confirm_button_confirm);
        this.cancel = ViewUtils.findView(this, R.id.confirm_button_cancel);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (helper != null) {
            helper.attach(ConfirmState.class, this::render);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (helper != null) {
            helper.detach();
        }
    }

    private void render(@Nonnull ConfirmState state) {
        renderTitle(state);
        renderMessage(state);
        renderConfirm(state);
        renderCancel(state);
    }

    private void renderTitle(ConfirmState state) {
        title.setText(state.title());
    }

    private void renderMessage(ConfirmState state) {
        message.setText(state.message());
    }

    private void renderConfirm(ConfirmState state) {
        confirm.setText(state.confirmText());
        confirm.setOnClickListener(v -> helper.store().dispatch(new ConfirmAction().confirmed(true)));
    }

    private void renderCancel(ConfirmState state) {
        cancel.setText(state.cancelText());
        cancel.setOnClickListener(v -> helper.store().dispatch(new ConfirmAction().confirmed(false)));
    }
}
