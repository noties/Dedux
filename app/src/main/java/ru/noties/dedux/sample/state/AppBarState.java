package ru.noties.dedux.sample.state;

import ru.noties.dedux.sample.state.core.BaseState;

public class AppBarState extends BaseState {

    public boolean closeVisible;
    public String title;

    public AppBarState() {

    }

    public AppBarState(boolean closeVisible, String title) {
        this.closeVisible = closeVisible;
        this.title = title;
    }

    @Override
    public String toString() {
        return "AppBarState{" +
                "closeVisible=" + closeVisible +
                ", title='" + title + '\'' +
                '}';
    }
}
