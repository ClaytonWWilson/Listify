package com.example.listify.data;

public class User {
    String cognitoID;
    String email;

    public User(String cognitoID, String email) {
        this.cognitoID = cognitoID;
        this.email = email;
    }

    public String getCognitoID() {
        return cognitoID;
    }

    public void setCognitoID(String cognitoID) {
        this.cognitoID = cognitoID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
