package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.listify.R;
import static com.example.listify.MainActivity.am;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordPage extends AppCompatActivity implements CodePage.CodeDialogListener {
    private Button button1; //Code page button

    String email;
    String newPassword;
    String confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpswd);

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
                am.confirmPasswordReset(newPassword, code);
            }
            catch (Exception e) {
                Log.i("Authentication", e.toString());
                TextView invalidCred = findViewById(R.id.textView6);
                invalidCred.setText("Password criteria not met. Please try again.");
            }
        }

        Intent intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
        startActivity(intent);
    }
}