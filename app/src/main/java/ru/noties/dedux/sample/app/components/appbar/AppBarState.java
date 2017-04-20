package ru.noties.dedux.sample.app.components.appbar;

import ru.noties.dedux.sample.state.BaseState;

public class AppBarState extends BaseState {

    private String title;
    private boolean filterEnabled;
    private boolean clearEnabled;
    private boolean allDone;
    private boolean toggleDoneEnabled;

    public AppBarState() {}

    public AppBarState(String title, boolean filterEnabled, boolean clearEnabled, boolean allDone, boolean toggleDoneEnabled) {
        this.title = title;
        this.filterEnabled = filterEnabled;
        this.clearEnabled = clearEnabled;
        this.allDone = allDone;
        this.toggleDoneEnabled = toggleDoneEnabled;
    }

    public String title() {
        return title;
    }

    public AppBarState title(String title) {
        this.title = title;
        return this;
    }

    public boolean filterEnabled() {
        return filterEnabled;
    }

    public AppBarState filterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
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
                ", filterEnabled=" + filterEnabled +
                ", clearEnabled=" + clearEnabled +
                ", allDone=" + allDone +
                ", toggleDoneEnabled=" + toggleDoneEnabled +
                '}';
    }
}
