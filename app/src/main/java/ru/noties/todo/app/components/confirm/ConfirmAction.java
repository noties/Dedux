package ru.noties.todo.app.components.confirm;

import dedux.Action;

public class ConfirmAction implements Action {

    private boolean confirmed;

    public boolean confirmed() {
        return confirmed;
    }

    public ConfirmAction confirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    @Override
    public String toString() {
        return "ConfirmAction{" +
                "confirmed=" + confirmed +
                '}';
    }
}
