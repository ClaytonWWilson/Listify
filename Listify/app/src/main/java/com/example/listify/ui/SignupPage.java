package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.listify.R;
import com.example.listify.AuthManager;
import com.example.listify.MainActivity;
import static com.example.listify.MainActivity.am;

import androidx.appcompat.app.AppCompatActivity;

public class SignupPage extends AppCompatActivity implements CodePage.CodeDialogListener {
    private Button button1; //Log in page button
    private Button button2; //Sign up button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailText = (EditText) findViewById(R.id.editTextTextEmailAddress);
                EditText passwordText = (EditText) findViewById(R.id.editTextTextPassword);

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                try {
                    am.startSignUp(email, password);
                }
                catch(Exception e) {
                    return;
                }

                openDialog();
            }
        });
    }

    public void openDialog() {
        CodePage codePage = new CodePage();
        codePage.show(getSupportFragmentManager(), "Verification code");
    }

    @Override
    public void sendCode(String code, boolean cancel) {
        if(cancel) {
            //Remove user from database
        }
        else {
            try {
                am.confirmSignUp(code);
                Intent intent = new Intent(SignupPage.this, MainActivity.class);
                startActivity(intent);
            }
            catch (Exception e) {
                //Remove user from database
            }
        }
    }
}