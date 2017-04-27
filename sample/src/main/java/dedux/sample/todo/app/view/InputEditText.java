package dedux.sample.todo.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class InputEditText extends EditText {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selStart, int selEnd);
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    private OnSelectionChangedListener onSelectionChangedListener;
    private OnBackPressedListener onBackPressedListener;

    public InputEditText(Context context) {
        super(context);
        init(context, null);
    }

    public InputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener onSelectionChangedListener) {
        this.onSelectionChangedListener = onSelectionChangedListener;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (onSelectionChangedListener != null) {
            onSelectionChangedListener.onSelectionChanged(selStart, selEnd);
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onBackPressedListener != null && hasFocus()) {
            if (KeyEvent.KEYCODE_BACK == keyCode) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    onBackPressedListener.onBackPressed();
                }
                return true;
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
