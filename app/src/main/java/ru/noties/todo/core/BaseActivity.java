package ru.noties.todo.core;

import android.app.Activity;

import ru.noties.todo.sample.R;

public abstract class BaseActivity extends Activity {

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.anim_activity_out);
    }
}
