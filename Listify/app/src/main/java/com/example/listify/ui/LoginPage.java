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

public class LoginPage extends AppCompatActivity {
    private Button button1; //Sign up page button
    private Button button2; //Forgot password button
    private Button button3; //Log in button

    @Override
    public void onBackPressed() {
        String prev = getIntent().getStringExtra("prev");

        if (prev != null && (prev.equals("Sign up") || prev.equals("Forgot password"))) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignupPage.class);
                intent.putExtra("prev", "Log in");
                startActivity(intent);
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, ForgotPasswordPage.class);
                intent.putExtra("prev", "Log in");
                startActivity(intent);
            }
        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailText = (EditText) findViewById(R.id.editTextTextPersonName);
                EditText passwordText = (EditText) findViewById(R.id.editTextTextPassword);

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                try {
                    am.signIn(email, password);
                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    intent.putExtra("prev", "Login");
                    startActivity(intent);
                }
                catch(Exception e) {
                    am.nullify();
                    Log.i("Authentication", e.toString());
                    TextView invalidCred = findViewById(R.id.textView5);
                    invalidCred.setText("Incorrect email or password. Please try again.");
                }
            }
        });
    }
}