package ru.noties.todo.app;

import android.os.Bundle;

import ru.noties.todo.app.navigation.core.NavigationComponent;
import ru.noties.todo.core.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(new NavigationComponent(this));
    }
}
