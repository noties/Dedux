package ru.noties.todo.app.account;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import ru.noties.debug.Debug;
import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.app.navigation.core.NavigationCloseAccountAction;
import ru.noties.todo.core.InputEditText;
import ru.noties.todo.core.TextWatcherAdapter;
import ru.noties.todo.sample.R;
import ru.noties.todo.utils.KeyboardUtils;
import ru.noties.todo.utils.ViewUtils;

public class AccountInputComponent extends FrameLayout {

    private ComponentHelper helper;

    private InputEditText email;
    private InputEditText password;

    private View logIn;

    private View errorRoot;
    private TextView errorText;
    private TextView errorAction;

    private boolean selfChange;

    private ViewTreeObserver.OnPreDrawListener errorOnPreDrawListener;

    public AccountInputComponent(Context context) {
        super(context);
        init(context, null);
    }

    public AccountInputComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        helper = ComponentHelper.install(context);
        if (helper == null) {
            if (!isInEditMode()) {
                throw new IllegalStateException();
            }
        }

        inflate(context, R.layout.view_account_input, this);

        this.email = ViewUtils.findView(this, R.id.account_input_email);
        this.password = ViewUtils.findView(this, R.id.account_input_password);

        this.logIn = findViewById(R.id.account_input_log_in);

        this.errorRoot = findViewById(R.id.account_input_error);
        this.errorText = ViewUtils.findView(errorRoot, R.id.account_input_error_text);
        this.errorAction = ViewUtils.findView(errorRoot, R.id.account_input_error_action);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (helper != null) {

            helper.attach(AccountAuthState.class, this::render);

            logIn.setOnClickListener(v -> logIn());

            email.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!selfChange) {
                        email.setError(null);
                        final String email = TextUtils.isEmpty(s)
                                ? null
                                : s.toString();
                        helper.store().dispatch(new AccountEmailChangedAction(email));
                    }
                }
            });

            password.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!selfChange) {
                        password.setError(null);
                    }
                }
            });

            final InputEditText.OnBackPressedListener onBackPressedListener = () -> {
                KeyboardUtils.hide(email.hasFocus() ? email : password);
                helper.store().dispatch(new NavigationCloseAccountAction());
            };

            email.setOnBackPressedListener(onBackPressedListener);
            password.setOnBackPressedListener(onBackPressedListener);

            email.setOnEditorActionListener((v, actionId, event) -> {
                password.post(() -> password.requestFocus());
                return true;
            });

            password.setOnEditorActionListener((v, actionId, event) -> {
                // execute all the checks
                logIn();
                return true;
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (helper != null) {
            helper.detach();
        }
    }

    private void render(@Nonnull AccountAuthState state) {
        selfChange = true;
        renderEmail(state);
        renderPassword(state);
        renderSignUpFirst(state);
        selfChange = false;
    }

    private void renderEmail(@Nonnull AccountAuthState state) {

        final String stateEmail = state.email();

        if (!TextUtils.equals(stateEmail, email.getText())) {

            email.setText(stateEmail);
            email.setSelection(email.length());

            // if we have email in input field, just focus on password
            if (!TextUtils.isEmpty(stateEmail)) {
                password.post(() -> password.requestFocus());
            }
        }

        email.setError(state.emailInputError());
    }

    private void renderPassword(@Nonnull AccountAuthState state) {
        password.setError(state.passwordInputError());
    }

    private void renderSignUpFirst(@Nonnull AccountAuthState state) {

        final boolean visible = state.signUpFirst();

        Debug.i(visible);

        if (visible) {
            errorText.setText(R.string.account_log_in_error_sign_up_first_text);
            errorAction.setText(R.string.account_log_in_error_sign_up_first_action);
            errorAction.setOnClickListener(v -> logIn());
        } else {
            errorText.setText(null);
            errorAction.setText(null);
            errorAction.setOnClickListener(null);
        }

        if (errorOnPreDrawListener != null) {
            errorRoot.getViewTreeObserver().removeOnPreDrawListener(errorOnPreDrawListener);
            errorOnPreDrawListener = null;
        }

        if (!visible && errorRoot.getHeight() == 0) {
            errorOnPreDrawListener = () -> {
                if (errorRoot.getHeight() != 0) {
                    errorRoot.getViewTreeObserver().removeOnPreDrawListener(errorOnPreDrawListener);
                    errorOnPreDrawListener = null;
                    renderSignUpFirst(state);
                    return true;
                }
                return false;
            };
            errorRoot.getViewTreeObserver().addOnPreDrawListener(errorOnPreDrawListener);
            return;
        }

        final float y = visible
                ? .0F
                : errorRoot.getHeight();

        if (Float.compare(errorRoot.getTranslationY(), y) != 0) {
            errorRoot.clearAnimation();
            errorRoot.animate()
                    .translationY(y)
                    .setDuration(250L)
                    .start();
        }
    }

    private void logIn() {

        final CharSequence emailInput = email.getText();
        final CharSequence passwordInput = password.getText();

        final String outEmail = TextUtils.isEmpty(emailInput)
                ? null
                : emailInput.toString();

        final String outPassword = TextUtils.isEmpty(passwordInput)
                ? null
                : passwordInput.toString();

        helper.store().dispatch(new AccountLogInAction(outEmail, outPassword));
    }
}
