package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
                EditText confirmPasswordText = (EditText) findViewById(R.id.editTextTextPassword2);

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String confirmPassword = confirmPasswordText.getText().toString();

                if(!password.equals(confirmPassword)) {
                    TextView invalidCred = findViewById(R.id.textView3);
                    invalidCred.setText("\"Confirm Password\" does not match \"Password\".");
                    return;
                }

                try {
                    am.startSignUp(email, password);
                }
                catch (Exception e) {
                    Log.i("Authentication", e.toString());
                    TextView invalidCred = findViewById(R.id.textView3);
                    invalidCred.setText("Invalid credentials. Please try again.");
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
        if(!cancel) {
            try {
                am.confirmSignUp(code);
                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent);
            }
            catch (Exception e) {
                Log.i("Authentication", e.toString());
            }
        }
    }
}