package ru.noties.todo.app.todo.input;

import javax.annotation.Nonnull;

import dedux.Action;

public class InputAction implements Action {

    public enum Type {
        FOCUS
        , INPUT
        , SELECTION
    }

    private final Type type;

    private boolean hasFocus;
    private String currentInput;
    private int selectionStart;
    private int selectionEnd;

    public InputAction() {
        this(Type.FOCUS);
    }

    public InputAction(@Nonnull Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public InputAction hasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        return this;
    }

    public String currentInput() {
        return currentInput;
    }

    public InputAction currentInput(String currentInput) {
        this.currentInput = currentInput;
        return this;
    }

    public int selectionStart() {
        return selectionStart;
    }

    public InputAction selectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
        return this;
    }

    public int selectionEnd() {
        return selectionEnd;
    }

    public InputAction selectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
        return this;
    }

    @Override
    public String toString() {
        return "InputAction{" +
                "type=" + type +
                ", hasFocus=" + hasFocus +
                ", currentInput='" + currentInput + '\'' +
                ", selectionStart=" + selectionStart +
                ", selectionEnd=" + selectionEnd +
                '}';
    }
}
