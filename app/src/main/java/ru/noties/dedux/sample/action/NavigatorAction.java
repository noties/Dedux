package ru.noties.dedux.sample.action;

import dedux.Action;

public class NavigatorAction implements Action {

    public final boolean back;
    public final boolean close;

    public NavigatorAction(boolean back, boolean close) {
        this.back = back;
        this.close = close;
    }
}
