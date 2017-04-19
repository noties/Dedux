package ru.noties.dedux.sample.state;

import ru.noties.dedux.sample.state.core.BaseState;

public class AppState extends BaseState {

    public AppScreen appScreen;

    public AppState() {
        this(AppScreen.LIST);
    }

    public AppState(AppScreen appScreen) {
        this.appScreen = appScreen;
    }

    @Override
    public String toString() {
        return "AppState{" +
                "appScreen=" + appScreen +
                '}';
    }
}
