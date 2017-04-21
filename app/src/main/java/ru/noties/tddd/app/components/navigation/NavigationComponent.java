package ru.noties.tddd.app.components.navigation;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javax.annotation.Nullable;

import ru.noties.tddd.app.components.AppComponent;
import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.app.components.account.AccountAuthComponent;
import ru.noties.tddd.app.components.confirm.ConfirmAction;
import ru.noties.tddd.app.components.confirm.ConfirmComponent;
import ru.noties.tddd.app.model.CloseAccountAction;

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

    private void render(@Nullable NavigationState state) {
        renderApp(state);
        renderAccount(state);
        renderConfirmClearDoneAction(state);
    }

    private void renderApp(@Nullable NavigationState state) {
        // in order to make this component `dump` we should follow the directions that came from state
        // but as we have decided to always show app & use a accountDialog for account
        // we will always render app
        if (getChildCount() == 0) {
            addView(new AppComponent(getContext()));
        }
    }

    private void renderAccount(@Nullable NavigationState state) {

        final boolean show = state != null && state.showAccount();

        if (show) {
            if (accountDialog == null
                    || !accountDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(new AccountAuthComponent(getContext()));
                dialog.setOnDismissListener(di -> {
                    helper.store().dispatch(new CloseAccountAction());
                });
                final Window window = dialog.getWindow();
                if (window != null) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                this.accountDialog = dialog;
                this.accountDialog.show();
            }
        } else {
            if (accountDialog != null
                    && accountDialog.isShowing()) {
                accountDialog.dismiss();
                accountDialog = null;
            }
        }
    }

    private void renderConfirmClearDoneAction(@Nullable NavigationState state) {

        final boolean showConfirm = state != null && state.showConfirm();

        if (showConfirm) {
            if (confirmDialog == null
                    || !confirmDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(new ConfirmComponent(getContext()));
                dialog.setOnDismissListener(di -> helper.store().dispatch(new ConfirmAction()));
                this.confirmDialog = dialog;
                this.confirmDialog.show();
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
