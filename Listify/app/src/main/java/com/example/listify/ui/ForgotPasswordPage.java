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

import com.example.listify.MainActivity;
import com.example.listify.R;
import static com.example.listify.MainActivity.am;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordPage extends AppCompatActivity {
    private Button button1; //Code page button

    String email;
    String newPassword;
    String confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpswd);

        if(am.getEmail() != null) {
            Intent intent = new Intent(ForgotPasswordPage.this, MainActivity.class);
            startActivity(intent);
        }

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailText = (EditText) findViewById(R.id.editTextTextEmailAddress);
                EditText newPasswordText = (EditText) findViewById(R.id.editTextTextPassword);
                EditText confirmNewPasswordText = (EditText) findViewById(R.id.editTextTextPassword2);

                email = emailText.getText().toString();
                newPassword = newPasswordText.getText().toString();
                confirmNewPassword = confirmNewPasswordText.getText().toString();

                if(!newPassword.equals(confirmNewPassword)) {
                    TextView invalidCred = findViewById(R.id.textView6);
                    invalidCred.setText("\"Confirm New Password\" does not match \"New Password\".");
                    return;
                }

                try {
                    am.changePassword(email);
                }
                catch (Exception e) {
                    Log.i("Authentication", e.toString());
                    TextView invalidCred = findViewById(R.id.textView6);
                    invalidCred.setText("Password criteria not met. Please try again.");
                    return;
                }

                View codeView = getLayoutInflater().inflate(R.layout.activity_code, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordPage.this);
                builder.setView(codeView);
                builder.setTitle("Verification code");
                builder.setMessage("Please enter the 6-digit verification code sent to your email.");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText codeText = (EditText) codeView.findViewById(R.id.editTextCode);
                        String code = codeText.getText().toString();
                        try {
                            am.confirmPasswordReset(newPassword, code);
                            Intent intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                        catch (Exception e) {
                            Log.i("Authentication", e.toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}