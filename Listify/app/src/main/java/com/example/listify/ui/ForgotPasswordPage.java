package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.listify.R;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordPage extends AppCompatActivity implements CodePage.CodeDialogListener {
    private Button button1; //Code page button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpswd);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        Intent intent;

        if(cancel) {
            intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
        }
        else {
            intent = new Intent(ForgotPasswordPage.this, ResetPasswordPage.class);
        }

        startActivity(intent);
    }
}