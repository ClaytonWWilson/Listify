package com.example.listify.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.listify.MainActivity
import com.example.listify.R

class LoginPage : AppCompatActivity() {
    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null

    override fun onBackPressed() {
        val prev = intent.getStringExtra("prev")
        if (prev != null && (prev == "Sign up" || prev == "Forgot password")) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button1 = findViewById<View>(R.id.button1) as Button
        button1!!.setOnClickListener {
            val intent = Intent(this@LoginPage, SignupPage::class.java)
            intent.putExtra("prev", "Log in")
            startActivity(intent)
        }
        button2 = findViewById<View>(R.id.button2) as Button
        button2!!.setOnClickListener {
            val intent = Intent(this@LoginPage, ForgotPasswordPage::class.java)
            intent.putExtra("prev", "Log in")
            startActivity(intent)
        }
        button3 = findViewById<View>(R.id.button3) as Button
        button3!!.setOnClickListener {
            val emailText = findViewById<View>(R.id.editTextTextPersonName) as EditText
            val passwordText = findViewById<View>(R.id.editTextTextPassword) as EditText
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            try {
                MainActivity.am.signIn(email, password)
                val intent = Intent(this@LoginPage, MainActivity::class.java)
                intent.putExtra("prev", "Login")
                startActivity(intent)
            } catch (e: Exception) {
                MainActivity.am.nullify()
                Log.i("Authentication", e.toString())
                val invalidCred = findViewById<TextView>(R.id.textView5)
                invalidCred.text = "Incorrect email or password. Please try again."
            }
        }
    }
}