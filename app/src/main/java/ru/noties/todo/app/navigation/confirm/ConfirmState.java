package ru.noties.todo.app.navigation.confirm;

import dedux.StateItemBase;

public class ConfirmState extends StateItemBase {

    private String title;
    private String message;
    private String confirmText;
    private String cancelText;

    public String title() {
        return title;
    }

    public ConfirmState title(String title) {
        this.title = title;
        return this;
    }

    public String message() {
        return message;
    }

    public ConfirmState message(String message) {
        this.message = message;
        return this;
    }

    public String confirmText() {
        return confirmText;
    }

    public ConfirmState confirmText(String confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    public String cancelText() {
        return cancelText;
    }

    public ConfirmState cancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    @Override
    public String toString() {
        return "ConfirmState{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", confirmText='" + confirmText + '\'' +
                ", cancelText='" + cancelText + '\'' +
                '}';
    }
}
