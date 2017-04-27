package ru.noties.todo.store.state;

import dedux.StateItemBase;

public class AppBarState extends StateItemBase {

    private String title;
    private boolean clearEnabled;
    private boolean allDone;
    private boolean toggleDoneEnabled;
    private int doneCount;

    public AppBarState() {
    }

    public String title() {
        return title;
    }

    public AppBarState title(String title) {
        this.title = title;
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

    public int doneCount() {
        return doneCount;
    }

    public AppBarState doneCount(int doneCount) {
        this.doneCount = doneCount;
        return this;
    }

    @Override
    public String toString() {
        return "AppBarState{" +
                "title='" + title + '\'' +
                ", clearEnabled=" + clearEnabled +
                ", allDone=" + allDone +
                ", toggleDoneEnabled=" + toggleDoneEnabled +
                ", doneCount=" + doneCount +
                '}';
    }
}
