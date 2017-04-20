package ru.noties.dedux.sample.app.components.appbar;

import ru.noties.dedux.sample.state.BaseState;

public class AppBarState extends BaseState {

    public enum ActionState {
        INVISIBLE
        , CLEAR
        , EMPTY
        ;
    }

    private String title;
    private boolean filterEnabled;
    private ActionState actionState;

    public AppBarState() {}

    public AppBarState(String title, boolean filterEnabled, ActionState actionState) {
        this.title = title;
        this.filterEnabled = filterEnabled;
        this.actionState = actionState;
    }

    public String title() {
        return title;
    }

    public boolean filterEnabled() {
        return filterEnabled;
    }

    public ActionState actionState() {
        return actionState;
    }

    @Override
    public String toString() {
        return "AppBarState{" +
                "title='" + title + '\'' +
                ", filterEnabled=" + filterEnabled +
                ", actionState=" + actionState +
                '}';
    }
}
