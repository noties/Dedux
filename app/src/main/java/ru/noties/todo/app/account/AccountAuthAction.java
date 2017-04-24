package ru.noties.todo.app.account;

import dedux.Action;

public class AccountAuthAction implements Action {

    private final boolean isLoggedIn;
    private final boolean signUpFirst;
    private final String emailInputError;
    private final String passwordInputError;

    public AccountAuthAction(boolean isLoggedIn, boolean signUpFirst, String emailInputError, String passwordInputError) {
        this.isLoggedIn = isLoggedIn;
        this.signUpFirst = signUpFirst;
        this.emailInputError = emailInputError;
        this.passwordInputError = passwordInputError;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean signUpFirst() {
        return signUpFirst;
    }

    public String emailInputError() {
        return emailInputError;
    }

    public String passwordInputError() {
        return passwordInputError;
    }

    @Override
    public String toString() {
        return "AccountAuthAction{" +
                "isLoggedIn=" + isLoggedIn +
                ", signUpFirst=" + signUpFirst +
                ", emailInputError='" + emailInputError + '\'' +
                ", passwordInputError='" + passwordInputError + '\'' +
                '}';
    }
}
