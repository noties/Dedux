package ru.noties.todo.app.todo.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.annotation.Nonnull;

import ru.noties.todo.app.ComponentHelper;
import ru.noties.todo.app.todo.core.AddTodoAction;
import ru.noties.todo.core.InputEditText;
import ru.noties.todo.core.TextWatcherAdapter;
import ru.noties.todo.sample.R;
import ru.noties.todo.utils.KeyboardUtils;
import ru.noties.todo.utils.StringUtils;
import ru.noties.todo.utils.ViewUtils;

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

    private void render(@Nonnull InputState state) {

        selfChange = true;

        renderFocus(state);
        renderText(state);

        // we cannot render selection here, we will do it only if text is different
//        renderSelection(state);

        selfChange = false;
    }

    private void renderFocus(@Nonnull InputState state) {

        final boolean hasFocus = state.hasFocus();

        if (hasFocus) {
            editText.requestFocus();
            KeyboardUtils.show(editText);
        } else {
            KeyboardUtils.hide(editText);
            focus.requestFocus();
        }
    }

    private void renderText(@Nonnull InputState state) {

        final String text = state.currentInput();

        if (!TextUtils.equals(text, editText.getText())) {

            editText.setText(text);

            // we will render selection only if we have applied new text state
            // otherwise cursor will never move from position we have stored
            renderSelection(state);
        }
    }

    private void renderSelection(@Nonnull InputState state) {

        final int s = state.selectionStart();

        final int e = state.selectionEnd();

        final int currentS = editText.getSelectionStart();
        final int currentE = editText.getSelectionEnd();

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
