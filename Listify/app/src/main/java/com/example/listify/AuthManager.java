package com.example.listify;

import android.content.Context;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.example.listify.data.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class AuthManager {
    AWSCognitoAuthSession authSession = null;
    AuthSignUpResult authSignUpResult = null;
    AuthSignInResult authSignInResult = null;
    AuthResetPasswordResult authResetPasswordResult = null;
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

    public AWSCognitoAuthSession getAuthSession() throws AuthException {
        if (authSession == null) {
            fetchAuthSession();
        }

        return authSession;
    }

    public String getUserToken() {
        if (authSession == null) {
            try {
                fetchAuthSession();
            } catch (AuthException e) {
                e.printStackTrace();
                return "";
            }
        }
        if (authSession.isSignedIn() == false) {
            return "";
        }
        return authSession.getUserPoolTokens().getValue().getIdToken();
    }


    public void setAuthSession(AuthSession toSet) {
        authSession = (AWSCognitoAuthSession) toSet;
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
        waiting = false;
    }

    public void setAuthSignInResult(AuthSignInResult toSet) {
        authSignInResult = toSet;
        waiting = false;
    }

    public void setAuthResetPasswordResult(AuthResetPasswordResult toSet) {
        authResetPasswordResult = toSet;
        waiting = false;
    }

    public void signOutSuccess() {
        waiting = false;
    }

    public void passwordResetSuccess() {
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

    public void deleteUser(Requestor requestor) throws AuthException {
        requestor.deleteObject("N/A", User.class);
        signOutUser();
    }

    public void signOutUser() throws AuthException {
        authSession = null;
        waiting = true;
        Amplify.Auth.signOut(this::signOutSuccess, error -> setAuthError(error));
        throwIfAuthError();
    }

    public void changePassword(String email) throws AuthException {
        this.email = email;
        //waiting = true;
        Amplify.Auth.resetPassword(email, result -> setAuthResetPasswordResult(result), error -> setAuthError(error));
        throwIfAuthError();
    }

    public void confirmPasswordReset(String newPassword, String confirmationCode) throws AuthException {
        //waiting = true;
        Amplify.Auth.confirmResetPassword(newPassword, confirmationCode, this::passwordResetSuccess, this::setAuthError);
        throwIfAuthError();
    }

    public static Properties loadProperties(Context context, String path) throws IOException, JSONException {
        Properties toReturn = new Properties();
        String propertiesJSONString = "";
        for (Object line : new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.auths))).lines().toArray()) {
            propertiesJSONString += line.toString();
        }
        System.out.println(propertiesJSONString);
        JSONObject propertiesJSON = new JSONObject(propertiesJSONString);
        propertiesJSON.keys().forEachRemaining(key -> {
            try {
                toReturn.setProperty(key, propertiesJSON.get(key).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        System.out.println(toReturn);
        return toReturn;
    }
}
