package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.listify.R;

import androidx.appcompat.app.AppCompatActivity;

public class CodePage extends AppCompatActivity {
    private Button button1; //Reset password page button
    private Button button2; //Cancel button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodePage.this, ResetPasswordPage.class);
                startActivity(intent);
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodePage.this, LoginPage.class);
                startActivity(intent);
            }
        });
    }
}