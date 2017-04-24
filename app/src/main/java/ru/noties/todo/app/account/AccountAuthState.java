package ru.noties.todo.app.account;

import dedux.StateItemBase;

public class AccountAuthState extends StateItemBase {

    private boolean isLoggedIn;
    private String email;

    private String emailInputError;

    private transient String passwordInputError;

    private transient boolean signUpFirst;

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

    public String emailInputError() {
        return emailInputError;
    }

    public AccountAuthState emailInputError(String emailInputError) {
        this.emailInputError = emailInputError;
        return this;
    }

    public String passwordInputError() {
        return passwordInputError;
    }

    public AccountAuthState passwordInputError(String passwordInputError) {
        this.passwordInputError = passwordInputError;
        return this;
    }

    public boolean signUpFirst() {
        return signUpFirst;
    }

    public AccountAuthState signUpFirst(boolean signUpFirst) {
        this.signUpFirst = signUpFirst;
        return this;
    }

    @Override
    public String toString() {
        return "AccountAuthState{" +
                "isLoggedIn=" + isLoggedIn +
                ", email='" + email + '\'' +
                ", emailInputError='" + emailInputError + '\'' +
                ", passwordInputError='" + passwordInputError + '\'' +
                ", signUpFirst=" + signUpFirst +
                '}';
    }
}
