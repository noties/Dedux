package ru.noties.tddd.app.components.navigation;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javax.annotation.Nullable;

import ru.noties.tddd.app.components.AppComponent;
import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.app.components.account.AccountAuthComponent;
import ru.noties.tddd.app.model.CloseAccountAction;

public class NavigationComponent extends FrameLayout {

    private ComponentHelper helper;

    private Dialog accountDialog;

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
        // but as we have decided to always show app & use a dialog for account
        // we will take a little step way from this thing and will always render `app`
        if (getChildCount() == 0) {
            addView(new AppComponent(getContext()));
        }
    }

    private void renderAccount(@Nullable NavigationState state) {
        final boolean show = state != null
                && state.showAccount();

        if (show) {
            if (accountDialog == null
                    || !accountDialog.isShowing()) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(new AccountAuthComponent(getContext()));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode) {
                            if (KeyEvent.ACTION_UP == event.getAction()) {
                                helper.store().dispatch(new CloseAccountAction());
                            }
                            return true;
                        }
                        return false;
                    }
                });
                final Window window = dialog.getWindow();
                if (window != null) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                accountDialog = dialog;
                accountDialog.show();
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

    }
}
