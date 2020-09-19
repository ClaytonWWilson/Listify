package com.example.listify;

import android.util.Log;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

public class AuthManager {
    AuthSession authSession = null;
    AuthSignUpResult authSignUpResult = null;
    AuthException authError = null;
    String email = null;
    String password = null;
    boolean waiting = false;


    public void fetchAuthSession() throws AuthException {
        waiting = true;
        Amplify.Auth.fetchAuthSession(
            result -> setAuthSession(result),
            error -> setAuthError(error)
        );
        throwIfAuthError();
    }


    public void setAuthSession(AuthSession toSet) {
        authSession = toSet;
    }

    public void setAuthError(AuthException newError) {
        authError = newError;
        waiting = false;
    }

    public void throwIfAuthError() throws AuthException{
        while (waiting);
        if (authError == null) {
            return;
        }
        AuthException toThrow = authError;
        authError = null;
        throw toThrow;
    }

    public void setAuthSignUpResult(AuthSignUpResult toSet) {
        authSignUpResult = toSet;
    }

    public void startSignup(String email, String password) throws AuthException {
        this.email = email;
        this.password = password;
        waiting = true;
        Amplify.Auth.signUp(
            email,
            password,
            AuthSignUpOptions.builder().build(),
            result -> setAuthSignUpResult(result),
            error -> setAuthError(error)
        );
        throwIfAuthError();

    }

    public void confirmSignUp(String confirmationCode) {
        Amplify.Auth.confirmSignUp(
            email,
            confirmationCode,
            result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
            error -> Log.e("AuthQuickstart", error.toString())
        );
    }

    public void signIn(String email, String password) {
        Amplify.Auth.signIn(
            email,
            password,
            result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete"),
            error -> Log.e("AuthQuickstart", error.toString())
        );
    }




}
