package ru.noties.dedux.sample.component;

import android.view.View;
import android.widget.TextView;

import ru.noties.dedux.sample.R;
import ru.noties.dedux.sample.action.GoBackAction;
import ru.noties.dedux.sample.component.core.BaseViewComponent;
import ru.noties.dedux.sample.state.AppBarState;
import ru.noties.dedux.sample.utils.ViewUtils;

public class AppBarComponent extends BaseViewComponent {

    private View close;
    private TextView title;

    @Override
    protected void onAttach() {

        this.title = ViewUtils.findView(view, R.id.app_bar_title);
        this.close = view.findViewById(R.id.app_bar_back);

        this.store.state()
                .get(AppBarState.class)
                .subscribe(true, compositeSubscription.compose(($1, s) -> renderState(s)));
    }

    private void renderState(AppBarState state) {
        renderTitle(state);
        renderClose(state);
    }

    private void renderTitle(AppBarState state) {
        title.setText(state.title);
    }

    private void renderClose(AppBarState state) {
        final boolean visible = state.closeVisible;
        final View.OnClickListener onClickListener;
        if (visible) {
            onClickListener = v -> store.dispatch(new GoBackAction());
        } else {
            onClickListener = null;
        }
        ViewUtils.setVisible(close, visible, View.INVISIBLE);
        close.setOnClickListener(onClickListener);
    }

    @Override
    protected void onDetach() {
        // composite subscription is unsubscribed in baseComponent
        // generally speaking we do not need to unreference our views here, but just in case
        title = null;
        close = null;
    }
}
