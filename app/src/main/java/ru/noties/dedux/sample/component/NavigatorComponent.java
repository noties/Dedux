package ru.noties.dedux.sample.component;

import ru.noties.dedux.sample.component.core.BaseActivityComponent;
import ru.noties.dedux.sample.state.NavigatorState;

public class NavigatorComponent extends BaseActivityComponent {

    @Override
    protected void onAttach() {
        store.state()
                .get(NavigatorState.class)
                .subscribe(false, compositeSubscription.compose(($1, s) -> applyState(s)));
    }

    private void applyState(NavigatorState state) {
        if (state != null) {
            if (state.finish) {
                activity.finish();
            } else if (state.goBack) {
                activity.onBackPressed();
            }
        }
    }

    @Override
    protected void onDetach() {

    }
}
