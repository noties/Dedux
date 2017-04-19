package ru.noties.dedux.sample.action;

import dedux.Action;
import ru.noties.dedux.sample.state.AppScreen;

public class AppStateAction implements Action {

    public final AppScreen screen;

    public AppStateAction(AppScreen screen) {
        this.screen = screen;
    }
}
