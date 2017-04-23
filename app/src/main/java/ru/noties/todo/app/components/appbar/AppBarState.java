package ru.noties.todo.app.components.appbar;

import ru.noties.todo.state.BaseState;

public class AppBarState extends BaseState {

    private String title;
    private boolean loggedIn;
    private boolean clearEnabled;
    private boolean allDone;
    private boolean toggleDoneEnabled;

    public AppBarState() {
    }

    public String title() {
        return title;
    }

    public AppBarState title(String title) {
        this.title = title;
        return this;
    }

    public boolean loggedIn() {
        return loggedIn;
    }

    public AppBarState loggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        return this;
    }

    public boolean clearEnabled() {
        return clearEnabled;
    }

    public AppBarState clearEnabled(boolean clearEnabled) {
        this.clearEnabled = clearEnabled;
        return this;
    }

    public boolean allDone() {
        return allDone;
    }

    public AppBarState allDone(boolean allDone) {
        this.allDone = allDone;
        return this;
    }

    public boolean toggleDoneEnabled() {
        return toggleDoneEnabled;
    }

    public AppBarState toggleDoneEnabled(boolean toggleDoneEnabled) {
        this.toggleDoneEnabled = toggleDoneEnabled;
        return this;
    }

    @Override
    public String toString() {
        return "AppBarState{" +
                "title='" + title + '\'' +
                ", loggedIn=" + loggedIn +
                ", clearEnabled=" + clearEnabled +
                ", allDone=" + allDone +
                ", toggleDoneEnabled=" + toggleDoneEnabled +
                '}';
    }
}
