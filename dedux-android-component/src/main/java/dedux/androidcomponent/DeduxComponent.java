package dedux.androidcomponent;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.StateItem;
import dedux.Store;

public abstract class DeduxComponent extends FrameLayout {

    private DeduxComponentHelper helper;
    private boolean isAttached;

    protected interface OnStateListener<T> {
        void apply(@Nonnull T state);
    }

    public DeduxComponent(Context context) {
        super(context);
        init(context, null);
    }

    public DeduxComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // searches current View
    public <V extends View> V findView(@IdRes int id) {
        return findView(this, id);
    }

    public <V extends View> V findView(@Nonnull View view, @IdRes int id) {
        //noinspection unchecked
        return (V) view.findViewById(id);
    }

    public boolean isAttached() {
        return isAttached;
    }

    protected Store store() {
        return helper.store();
    }

    protected abstract void onCreated(@Nonnull Context context, @Nullable AttributeSet set);

    // in this method it's safe to call subscribeTo
    protected abstract void onAttached();

    protected void onDetached() {

    }

    protected <T extends StateItem> void subscribeTo(@Nonnull Class<T> state, @Nonnull OnStateListener<? super T> onStateListener) {

        if (!isAttached) {
            throw new IllegalStateException("This view is not attached to a window, this: " + this);
        }

        helper.subscribeTo(state, onStateListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        onAttached();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        helper.unsubscribe();
        onDetached();
    }

    private void init(Context context, @Nullable AttributeSet attributeSet) {
        if (isInEditMode()) {
            helper = DeduxComponentHelperEditMode.install(context, attributeSet);
        } else {
            helper = DeduxComponentHelperImpl.install(context);
        }
        onCreated(context, attributeSet);
    }
}
