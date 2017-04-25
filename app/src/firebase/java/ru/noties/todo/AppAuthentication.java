package ru.noties.todo;

import com.google.firebase.auth.FirebaseAuth;

public class AppAuthentication {

    public static boolean isAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private AppAuthentication() {}
}
