package ru.noties.tddd.app.components.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import javax.annotation.Nullable;

import ru.noties.tddd.app.components.ComponentHelper;
import ru.noties.tddd.sample.R;
import ru.noties.tddd.utils.ViewUtils;

public class AccountAuthComponent extends LinearLayout {

    private ComponentHelper helper;

    private ViewGroup container;
    private Boolean prevLoggedIn;

    public AccountAuthComponent(Context context) {
        super(context);
        init(context, null);
    }

    public AccountAuthComponent(Context context, AttributeSet attrs) {
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

        inflate(context, R.layout.view_account, this);

        container = ViewUtils.findView(this, R.id.account_container);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (helper != null) {
            helper.attach(AccountAuthState.class, this::render);
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

        final boolean came = state != null && state.isLoggedIn();

        if (prevLoggedIn == null
                || prevLoggedIn != came) {

            final View view = came
                    ? null
                    : new AccountInputComponent(getContext());

            container.removeAllViews();
            container.addView(view);

            prevLoggedIn = came;
        }
    }
}
