package ru.noties.dedux.sample.app;

import android.os.Bundle;

import ru.noties.debug.Debug;
import ru.noties.dedux.sample.R;
import ru.noties.dedux.sample.component.AppBarComponent;
import ru.noties.dedux.sample.state.AppState;

public class ToDoActivity extends BaseActivity {

    private AppBarComponent appBarComponent;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(R.layout.activity_todo);

        appBarComponent = new AppBarComponent();
        appBarComponent.attach(findViewById(R.id.app_bar), store);

        store.state()
                .get(AppState.class)
                .subscribe(true, compositeSubscription.compose(($1, state) -> applyState(state)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        appBarComponent.detach();
    }

    // should not be null
    private void applyState(AppState appState) {

        Debug.i(appState.appScreen);

        switch (appState.appScreen) {

            case LIST:
                break;

            case INPUT:
                break;

            default:
                throw new RuntimeException("Unknown screen: " + appState.appScreen);
        }
    }
}
