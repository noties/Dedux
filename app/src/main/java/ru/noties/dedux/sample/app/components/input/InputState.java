package ru.noties.dedux.sample.app.components.input;

import ru.noties.dedux.sample.state.BaseState;

public class InputState extends BaseState {

    private boolean hasFocus;
    private String currentInput;
    private int selectionStart;
    private int selectionEnd;

    public InputState() {}

    public InputState(boolean hasFocus, String currentInput, int selectionStart, int selectionEnd) {
        this.hasFocus = hasFocus;
        this.currentInput = currentInput;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public String currentInput() {
        return currentInput;
    }

    public int selectionStart() {
        return selectionStart;
    }

    public int selectionEnd() {
        return selectionEnd;
    }

    public InputState hasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        return this;
    }

    public InputState currentInput(String currentInput) {
        this.currentInput = currentInput;
        return this;
    }

    public InputState selectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
        return this;
    }

    public InputState selectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
        return this;
    }

    @Override
    public String toString() {
        return "InputState{" +
                "hasFocus=" + hasFocus +
                ", currentInput='" + currentInput + '\'' +
                ", selectionStart=" + selectionStart +
                ", selectionEnd=" + selectionEnd +
                '}';
    }
}
