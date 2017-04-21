package ru.noties.tddd.app.components.account;

import ru.noties.tddd.state.BaseState;

public class AccountAuthState extends BaseState {

    private boolean isLoggedIn;
    private String email;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public AccountAuthState isLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        return this;
    }

    public String email() {
        return email;
    }

    public AccountAuthState email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "AccountAuthState{" +
                "isLoggedIn=" + isLoggedIn +
                '}';
    }
}
