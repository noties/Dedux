package dedux.sample.todo.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import dedux.sample.todo.R;
import dedux.sample.todo.store.action.ConfirmAction;
import dedux.sample.todo.store.state.ConfirmState;

public class ConfirmComponent extends DeduxComponent {

    private TextView title;
    private TextView message;
    private TextView confirm;
    private TextView cancel;

    public ConfirmComponent(Context context) {
        super(context);
    }

    public ConfirmComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.view_confirm, this);

        this.title = findView(R.id.confirm_title);
        this.message = findView(R.id.confirm_message);
        this.confirm = findView(R.id.confirm_button_confirm);
        this.cancel = findView(R.id.confirm_button_cancel);
    }

    @Override
    protected void onAttached() {
        subscribeTo(ConfirmState.class, this::render);
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
        confirm.setOnClickListener(v -> store().dispatch(new ConfirmAction().confirmed(true)));
    }

    private void renderCancel(ConfirmState state) {
        cancel.setText(state.cancelText());
        cancel.setOnClickListener(v -> store().dispatch(new ConfirmAction().confirmed(false)));
    }
}
