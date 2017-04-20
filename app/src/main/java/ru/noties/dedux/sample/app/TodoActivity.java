package ru.noties.dedux.sample.app;

import android.os.Bundle;

import ru.noties.dedux.sample.R;
import ru.noties.dedux.sample.app.core.BaseActivity;

public class TodoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        setContentView(R.layout.activity_todo);

    }
}
