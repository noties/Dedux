package ru.noties.todo.app;

import android.os.Bundle;

import ru.noties.todo.sample.R;
import ru.noties.todo.app.core.BaseActivity;

public class TodoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(R.layout.activity_todo);

    }
}
