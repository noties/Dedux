package ru.noties.todo.app.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.sample.R;
import ru.noties.todo.utils.ViewUtils;

public class AccountAuthComponent extends FrameLayout {

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

    private void render(@Nonnull AccountAuthState state) {

        final boolean came = state.isLoggedIn();

        if (prevLoggedIn == null
                || prevLoggedIn != came) {

            final View view = came
                    ? new TextView(getContext()) // todo
                    : new AccountInputComponent(getContext());

            container.removeAllViews();
            container.addView(view);

            prevLoggedIn = came;
        }
    }
}
