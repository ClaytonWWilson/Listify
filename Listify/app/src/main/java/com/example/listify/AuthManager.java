package com.example.listify;

import android.util.Log;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

public class AuthManager {
    AuthSession authSession = null;
    AuthSignUpResult authSignUpResult = null;
    AuthSignInResult authSignInResult = null;
    AuthException authError = null;
    String email = null;
    String password = null;
    volatile boolean waiting = false;


    void fetchAuthSession() throws AuthException {
        waiting = true;
        Amplify.Auth.fetchAuthSession(
            result -> setAuthSession(result),
            error -> setAuthError(error)
        );
        throwIfAuthError();
    }

    public AuthSession getAuthSession() throws AuthException {
        fetchAuthSession();
        return authSession;
    }


    public void setAuthSession(AuthSession toSet) {
        authSession = toSet;
        waiting = false;
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

    public void setAuthSignInResult(AuthSignInResult toSet) {
        authSignInResult = toSet;
        waiting = false;
    }

    public void startSignUp(String email, String password) throws AuthException {
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

    public void confirmSignUp(String confirmationCode) throws AuthException {
        waiting = true;
        Amplify.Auth.confirmSignUp(
            email,
            confirmationCode,
            result -> setAuthSignUpResult(result),
            error -> setAuthError(error)
        );
        throwIfAuthError();
    }

    public void signIn(String email, String password) throws AuthException{
        this.email = email;
        this.password = password;
        waiting = true;
        Amplify.Auth.signIn(
            email,
            password,
            result -> setAuthSignInResult(result),
            error -> setAuthError(error)
        );
        throwIfAuthError();
    }




}
