package dedux.sample.todo.app.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import javax.annotation.Nonnull;

public class KeyboardUtils {

    public static void show(@Nonnull View view) {
        final InputMethodManager manager
                = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, 0);
    }

    public static void hide(@Nonnull View view) {
        final InputMethodManager manager
                = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private KeyboardUtils() {
    }
}
