package com.example.listify.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.listify.Requestor;

import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

import androidx.appcompat.app.AppCompatActivity;

public class SignupPage extends AppCompatActivity {
    private Button button1; //Log in page button
    private Button button2; //Sign up button

    String email;
    String password;
    String confirmPassword;

    @Override
    public void onBackPressed() {
        String prev = getIntent().getStringExtra("prev");

        if (prev != null && (prev.equals("Log in") || prev.equals("Forgot password"))) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                intent.putExtra("prev", "Sign up");
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

                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                confirmPassword = confirmPasswordText.getText().toString();

                if(!password.equals(confirmPassword)) {
                    TextView invalidCred = findViewById(R.id.textView3);
                    invalidCred.setText("\"Confirm Password\" does not match \"Password\".");
                    return;
                }

                try {
                    am.startSignUp(email, password);
                }
                catch (Exception e) {
                    am.nullify();
                    Log.i("Authentication", e.toString());
                    TextView invalidCred = findViewById(R.id.textView3);
                    invalidCred.setText("Invalid credentials. Please try again.");
                    return;
                }

                View codeView = getLayoutInflater().inflate(R.layout.activity_code, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupPage.this);
                builder.setView(codeView);
                builder.setTitle("Verification code");
                builder.setMessage("Please enter the 6-digit verification code sent to your email.");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText codeText = (EditText) codeView.findViewById(R.id.editTextCode);
                        String code = codeText.getText().toString();
                        try {
                            am.confirmSignUp(code);
                            am.signIn(email, password);
                            Intent intent = new Intent(SignupPage.this, MainActivity.class);
                            intent.putExtra("prev", "Sign up");
                            startActivity(intent);
                        }
                        catch (Exception e) {
                            am.nullify();
                            Log.i("Authentication", e.toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}