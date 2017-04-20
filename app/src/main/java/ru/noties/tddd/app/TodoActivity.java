package ru.noties.tddd.app;

import android.os.Bundle;

import ru.noties.tddd.sample.R;
import ru.noties.tddd.app.core.BaseActivity;

public class TodoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(R.layout.activity_todo);

    }
}
