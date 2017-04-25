package ru.noties.todo.app.account;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dedux.Middleware;
import dedux.Store;
import ru.noties.todo.R;

public class AccountLogInMiddleware implements Middleware<AccountLogInAction> {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final String AUTH_ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";

    private final Resources resources;

    public AccountLogInMiddleware(Resources resources) {
        this.resources = resources;
    }

    @Override
    public void apply(@Nonnull Store store, @Nonnull AccountLogInAction action, @Nonnull Next next) {

        final String email = action.email();
        final String password = action.password();

        // validate both

        String emailValidationError = null;
        try {
            validateEmail(email);
        } catch (ValidationException e) {
            emailValidationError = resources.getString(e.errorResId, e.args);
        }

        String passwordValidationError = null;
        try {
            validatePassword(password);
        } catch (ValidationException e) {
            passwordValidationError = resources.getString(e.errorResId, e.args);
        }

        if (!TextUtils.isEmpty(emailValidationError)
                || !TextUtils.isEmpty(passwordValidationError)) {
            store.dispatch(new AccountAuthAction(false, false, emailValidationError, passwordValidationError));
            return;
        }

        final AccountAuthState authState = store.state().get(AccountAuthState.class).get();

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final Task<AuthResult> authTask;
        if (authState.signUpFirst()) {
            //noinspection ConstantConditions
            authTask = auth.createUserWithEmailAndPassword(email, password);
        } else {
            //noinspection ConstantConditions
            authTask = auth.signInWithEmailAndPassword(email, password);
        }

        //noinspection ConstantConditions
        authTask.addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        store.dispatch(new AccountAuthAction(true, false, null, null));
                        store.dispatch(new AccountAuthStateChangedAction(true));
                        return;
                    }

                    final Exception exception = task.getException();

                    if (exception instanceof FirebaseAuthException) {

                        final FirebaseAuthException authException = (FirebaseAuthException) exception;
                        final String errorCode = authException.getErrorCode();

                        if (!TextUtils.isEmpty(errorCode)) {

                            switch (errorCode) {

                                case AUTH_ERROR_USER_NOT_FOUND:
                                    store.dispatch(new AccountAuthAction(false, true, null, null));
                                    break;


                            }
                        }
                    }
                });
    }

    private static void validateEmail(@Nullable String email) throws ValidationException {

        if (TextUtils.isEmpty(email)) {
            throw new ValidationException(R.string.input_validation_field_required);
        }

        final Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        if (!matcher.matches()) {
            throw new ValidationException(R.string.input_validation_email_not_valid);
        }

        // ok
    }

    private static void validatePassword(@Nullable String password) throws ValidationException {

        if (TextUtils.isEmpty(password)) {
            throw new ValidationException(R.string.input_validation_field_required);
        }

        // primitive password length check
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException(R.string.input_validation_field_min_length, MIN_PASSWORD_LENGTH);
        }

        // ok
    }

    private static class ValidationException extends Exception {

        final int errorResId;
        final Object[] args;

        ValidationException(int errorResId, Object... args) {
            this.errorResId = errorResId;
            this.args = args;
        }
    }
}
