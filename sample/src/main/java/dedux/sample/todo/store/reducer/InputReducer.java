package dedux.sample.todo.store.reducer;

import javax.annotation.Nonnull;

import dedux.MutableState;
import dedux.Reducer;
import dedux.sample.todo.store.state.InputState;
import dedux.sample.todo.store.action.InputAction;

public class InputReducer implements Reducer<InputAction> {

    @Nonnull
    @Override
    public Class<InputAction> actionType() {
        return InputAction.class;
    }

    @Override
    public void reduce(@Nonnull MutableState state, @Nonnull InputAction inputAction) {
        final InputState current = state.get(InputState.class).get();
        final InputState out = current.clone(s -> {

            switch (inputAction.type()) {

                case FOCUS:
                    s.hasFocus(inputAction.hasFocus());
                    break;

                case INPUT:
                    s.currentInput(inputAction.currentInput());
                    break;

                case SELECTION:
                    s.selectionStart(inputAction.selectionStart());
                    s.selectionEnd(inputAction.selectionEnd());
                    break;
            }
        });
        state.set(out);
    }
}
