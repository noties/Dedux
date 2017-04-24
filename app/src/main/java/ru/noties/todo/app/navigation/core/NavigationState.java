package ru.noties.todo.app.navigation.core;

import dedux.StateItemBase;

public class NavigationState extends StateItemBase {

    private boolean showApp = true;
    private boolean showAccount;
    private boolean showConfirm;

    public boolean showApp() {
        return showApp;
    }

    public NavigationState showApp(boolean showApp) {
        this.showApp = showApp;
        return this;
    }

    public boolean showAccount() {
        return showAccount;
    }

    public NavigationState showAccount(boolean showAccount) {
        this.showAccount = showAccount;
        return this;
    }

    public boolean showConfirm() {
        return showConfirm;
    }

    public NavigationState showConfirm(boolean showConfirm) {
        this.showConfirm = showConfirm;
        return this;
    }

    @Override
    public String toString() {
        return "NavigationState{" +
                "showApp=" + showApp +
                ", showAccount=" + showAccount +
                ", showConfirm=" + showConfirm +
                '}';
    }
}
