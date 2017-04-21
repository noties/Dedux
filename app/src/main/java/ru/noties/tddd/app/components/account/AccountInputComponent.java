package ru.noties.tddd.app.components.account;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import javax.annotation.Nullable;

import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.app.core.TextWatcherAdapter;
import ru.noties.tddd.app.model.AccountEmailChangedAction;
import ru.noties.tddd.sample.R;
import ru.noties.tddd.utils.ViewUtils;

public class AccountInputComponent extends LinearLayout {

    private ComponentHelper helper;

    private EditText email;
    private EditText password;

    private boolean selfChange;

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

        setOrientation(VERTICAL);

        inflate(context, R.layout.view_account_input, this);

        this.email = ViewUtils.findView(this, R.id.account_input_email);
        this.password = ViewUtils.findView(this, R.id.account_input_password);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (helper != null) {
            helper.attach(AccountAuthState.class, this::render);

            email.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!selfChange) {
                        final String email = TextUtils.isEmpty(s)
                                ? null
                                : s.toString();
                        helper.store().dispatch(new AccountEmailChangedAction(email));
                    }
                }
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

    private void render(@Nullable AccountAuthState state) {
        selfChange = true;
        renderEmail(state);
        selfChange = false;
    }

    private void renderEmail(AccountAuthState state) {
        final String stateEmail = state != null
                ? state.email()
                : null;
        if (!TextUtils.equals(stateEmail, email.getText())) {
            email.setText(stateEmail);
            email.setSelection(email.length());
        }
    }
}
