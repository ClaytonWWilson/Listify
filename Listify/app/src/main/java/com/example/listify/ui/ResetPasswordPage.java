package com.example.listify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.listify.R;
import com.example.listify.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordPage extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpswd);

        button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}