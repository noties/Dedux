package ru.noties.todo.app.component;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.androidcomponent.DeduxComponent;
import ru.noties.todo.R;
import ru.noties.todo.store.action.InputAction;
import ru.noties.todo.store.state.InputState;
import ru.noties.todo.store.action.AddTodoAction;
import ru.noties.todo.app.view.InputEditText;
import ru.noties.todo.app.utils.TextWatcherAdapter;
import ru.noties.todo.app.utils.KeyboardUtils;
import ru.noties.todo.utils.StringUtils;

public class InputComponent extends DeduxComponent {

    private InputEditText editText;
    private TextWatcher textWatcher;

    private View focus;

    private boolean selfChange;

    public InputComponent(Context context) {
        super(context);
    }

    public InputComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreated(@Nonnull Context context, @Nullable AttributeSet set) {

        inflate(context, R.layout.view_input, this);

        this.editText = findView(R.id.input_edit_text);
        this.focus = findViewById(R.id.input_focus);
    }

    @Override
    protected void onAttached() {

        subscribeTo(InputState.class, this::render);

        textWatcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!selfChange) {
                    final String input = TextUtils.isEmpty(s)
                            ? null
                            : s.toString();
                    store().dispatch(new InputAction(InputAction.Type.INPUT).currentInput(input));
                }
            }
        };
        editText.addTextChangedListener(textWatcher);

        editText.setOnSelectionChangedListener((start, end) -> {
            if (!selfChange) {
                store()
                        .dispatch(new InputAction(InputAction.Type.SELECTION)
                                .selectionStart(start)
                                .selectionEnd(end));
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!selfChange) {
                store().dispatch(new InputAction().hasFocus(hasFocus));
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final CharSequence input = v.getText();
                if (TextUtils.isEmpty(input)) {
                    // do nothing
                    store().dispatch(new InputAction(InputAction.Type.FOCUS).hasFocus(false));
                } else {
                    store().dispatch(new AddTodoAction(input.toString()));
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

    @Override
    protected void onDetached() {
        super.onDetached();

        // strictly speaking we do not need this
        editText.removeTextChangedListener(textWatcher);
        textWatcher = null;
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
