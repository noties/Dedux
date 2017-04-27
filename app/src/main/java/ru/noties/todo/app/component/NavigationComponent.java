package ru.noties.todo.app.component;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import ru.noties.todo.R;
import ru.noties.todo.store.action.ConfirmAction;
import ru.noties.todo.store.state.NavigationState;

public class NavigationComponent extends DeduxComponent {

    private Dialog confirmDialog;

    public NavigationComponent(Context context) {
        super(context);
    }

    public NavigationComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {
        // no op
    }

    @Override
    protected void onAttached() {
        subscribeTo(NavigationState.class, this::render);
    }

    private void render(@Nonnull NavigationState state) {
        renderApp(state);
        renderConfirmClearDoneAction(state);
    }

    private void renderApp(@Nonnull NavigationState state) {
        // in order to make this component `dump` we should follow the directions that came from state
        // but as we have decided to always show app & use a accountDialog for account
        // we will always render app
        if (getChildCount() == 0) {
            addView(new AppComponent(getContext()));
        }
    }

    private void renderConfirmClearDoneAction(@Nonnull NavigationState state) {

        final boolean showConfirm = state.showConfirm();

        if (showConfirm) {
            if (confirmDialog == null
                    || !confirmDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
                dialog.setContentView(new ConfirmComponent(getContext()));
                dialog.setOnDismissListener(di -> store().dispatch(new ConfirmAction()));
                this.confirmDialog = dialog;
                this.confirmDialog.show();

                final Window window = dialog.getWindow();
                if (window != null) {
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.copyFrom(window.getAttributes());
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = LayoutParams.WRAP_CONTENT;
                    window.setAttributes(params);
                }
            }
        } else {
            if (confirmDialog != null
                    && confirmDialog.isShowing()) {
                confirmDialog.dismiss();
                confirmDialog = null;
            }
        }
    }
}
