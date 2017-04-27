package ru.noties.todo.app;

import android.os.Bundle;

import ru.noties.todo.app.component.NavigationComponent;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(new NavigationComponent(this));
    }
}
