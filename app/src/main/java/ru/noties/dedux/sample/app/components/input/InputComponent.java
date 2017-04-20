package ru.noties.dedux.sample.app.components.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import ru.noties.debug.Debug;
import ru.noties.dedux.sample.R;
import ru.noties.dedux.sample.app.model.AddTodoAction;
import ru.noties.dedux.sample.app.components.ComponentHelper;
import ru.noties.dedux.sample.app.core.TextWatcherAdapter;
import ru.noties.dedux.sample.utils.KeyboardUtils;
import ru.noties.dedux.sample.utils.StringUtils;
import ru.noties.dedux.sample.utils.ViewUtils;

public class InputComponent extends FrameLayout {

    private ComponentHelper helper;

    private InputEditText editText;
    private TextWatcher textWatcher;

    private View focus;

    private boolean selfChange;

    public InputComponent(Context context) {
        super(context);
        init(context, null);
    }

    public InputComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        helper = ComponentHelper.install(context);
        if (helper == null) {
            if (!isInEditMode()) {
                throw new IllegalStateException();
            }
        }

        inflate(context, R.layout.view_input, this);

        this.editText = ViewUtils.findView(this, R.id.input_edit_text);
        this.focus = findViewById(R.id.input_focus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (helper != null) {

            helper.attach(InputState.class, this::render);

            textWatcher = new TextWatcherAdapter() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!selfChange) {
                        final String input = TextUtils.isEmpty(s)
                                ? null
                                : s.toString();
                        helper.store().dispatch(new InputAction(InputAction.Type.INPUT).currentInput(input));
                    }
                }
            };
            editText.addTextChangedListener(textWatcher);

            editText.setOnSelectionChangedListener((start, end) -> {
                if (!selfChange) {
                    helper.store()
                            .dispatch(new InputAction(InputAction.Type.SELECTION)
                                    .selectionStart(start)
                                    .selectionEnd(end));
                }
            });

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                Debug.i(hasFocus, selfChange);
                if (!selfChange) {
                    helper.store().dispatch(new InputAction().hasFocus(hasFocus));
                }
            });

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    final CharSequence input = v.getText();
                    if (TextUtils.isEmpty(input)) {
                        // do nothing
                        helper.store().dispatch(new InputAction(InputAction.Type.FOCUS).hasFocus(false));
                    } else {
                        helper.store().dispatch(new AddTodoAction(input.toString()));
                    }
                    return true;
                }
            });

            editText.setOnBackPressedListener(() -> {
                editText.post(() -> {
                    editText.clearFocus();
                    focus.post(() -> focus.requestFocus());
                });
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (helper != null) {
            editText.removeTextChangedListener(textWatcher);
            textWatcher = null;
            helper.detach();
        }
    }

    private void render(@Nullable InputState state) {

        selfChange = true;

        renderFocus(state);
        renderText(state);

        // we cannot render selection here, we will do it only if text is different
//        renderSelection(state);

        selfChange = false;
    }

    private void renderFocus(@Nullable InputState state) {
        final boolean hasFocus = state != null && state.hasFocus();
        if (hasFocus) {
            editText.requestFocus();
            KeyboardUtils.show(editText);
        } else {
            KeyboardUtils.hide(editText);
            editText.post(() -> {
                editText.clearFocus();
                focus.post(() -> focus.requestFocus());
            });
        }
    }

    private void renderText(@Nullable InputState state) {
        // we will end up with recursive call...
        final String text = state != null
                ? state.currentInput()
                : null;
        if (!TextUtils.equals(text, editText.getText())) {

            editText.setText(text);

            // we will render selection only if we have applied new text state
            // otherwise cursor will never move from position we have stored
            renderSelection(state);
        }
    }

    private void renderSelection(@Nullable InputState state) {

        final int s = state != null
                ? state.selectionStart()
                : 0;

        final int e = state != null
                ? state.selectionEnd()
                : 0;

        final int currentS = editText.getSelectionStart();
        final int currentE = editText.getSelectionEnd();

        Debug.i(selfChange, s, e, currentS, currentE);

        if (s == 0 && e == 0) {
            editText.setSelection(0);
        } else if (s == currentS && e == currentE) {
            // no op
        } else {

            final int start;
            final int end;

            final int length = StringUtils.length(state.currentInput());

            start = s > length
                    ? length
                    : s < 0 ? 0 : s;

            end = e > length
                    ? length
                    : e < 0 ? 0 : e;

            editText.setSelection(start, end);
        }
    }
}
