package ru.noties.todo.app.navigation.core;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javax.annotation.Nonnull;

import ru.noties.todo.app.AppComponent;
import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.app.account.AccountAuthComponent;
import ru.noties.todo.app.navigation.confirm.ConfirmAction;
import ru.noties.todo.app.navigation.confirm.ConfirmComponent;
import ru.noties.todo.R;

public class NavigationComponent extends FrameLayout {

    private ComponentHelper helper;

    private Dialog accountDialog;
    private Dialog confirmDialog;

    public NavigationComponent(Context context) {
        super(context);
        init(context, null);
    }

    public NavigationComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        helper = ComponentHelper.install(context);
        if (helper == null
                && !isInEditMode()) {
            throw new IllegalStateException();
        }


        // for layout preview
        if (isInEditMode()) {
            addView(new AppComponent(context));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (helper != null) {
            helper.attach(NavigationState.class, this::render);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (helper != null) {
            helper.detach();
        }
    }

    private void render(@Nonnull NavigationState state) {
        renderApp(state);
        renderAccount(state);
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

    private void renderAccount(@Nonnull NavigationState state) {

        final boolean show = state.showAccount();

        if (show) {
            if (accountDialog == null
                    || !accountDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
                dialog.setContentView(new AccountAuthComponent(getContext()));
                dialog.setOnDismissListener(di -> {
                    helper.store().dispatch(new NavigationCloseAccountAction());
                });
                this.accountDialog = dialog;
                this.accountDialog.show();

                final Window window = dialog.getWindow();
                if (window != null) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.copyFrom(window.getAttributes());
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = LayoutParams.WRAP_CONTENT;
                    window.setAttributes(params);
                }
            }
        } else {
            if (accountDialog != null
                    && accountDialog.isShowing()) {
                accountDialog.dismiss();
                accountDialog = null;
            }
        }
    }

    private void renderConfirmClearDoneAction(@Nonnull NavigationState state) {

        final boolean showConfirm = state.showConfirm();

        if (showConfirm) {
            if (confirmDialog == null
                    || !confirmDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
                dialog.setContentView(new ConfirmComponent(getContext()));
                dialog.setOnDismissListener(di -> helper.store().dispatch(new ConfirmAction()));
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
