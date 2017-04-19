package ru.noties.dedux.sample.app;

import android.app.Activity;
import android.os.Bundle;

import dedux.CompositeSubscription;
import dedux.Store;
import ru.noties.dedux.sample.App;
import ru.noties.dedux.sample.action.GoBackAction;
import ru.noties.dedux.sample.state.NavigatorState;

public abstract class BaseActivity extends Activity {

    protected Store store;
    protected CompositeSubscription compositeSubscription;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        store = ((App) getApplication()).store();
        compositeSubscription = new CompositeSubscription();

        store.state()
                .get(NavigatorState.class)
                .subscribe(false, compositeSubscription.compose(($1, s) -> {
                    if (s != null) {
                        if (s.goBack) {
                            BaseActivity.super.onBackPressed();
                        } else if (s.finish) {
                            BaseActivity.super.finish();
                        }
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        store.dispatch(new GoBackAction());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
        compositeSubscription = null;
    }
}
