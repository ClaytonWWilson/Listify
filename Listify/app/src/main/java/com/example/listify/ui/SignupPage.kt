package com.example.listify.ui

import android.app.AlertDialog
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

class SignupPage : AppCompatActivity() {
    private var button1: Button? = null
    private var button2: Button? = null
    var email: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    override fun onBackPressed() {
        val prev = intent.getStringExtra("prev")
        if (prev != null && (prev == "Log in" || prev == "Forgot password")) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        button1 = findViewById<View>(R.id.button1) as Button
        button1!!.setOnClickListener {
            val intent = Intent(this@SignupPage, LoginPage::class.java)
            intent.putExtra("prev", "Sign up")
            startActivity(intent)
        }
        button2 = findViewById<View>(R.id.button2) as Button
        button2!!.setOnClickListener(View.OnClickListener {
            val emailText = findViewById<View>(R.id.editTextTextEmailAddress) as EditText
            val passwordText = findViewById<View>(R.id.editTextTextPassword) as EditText
            val confirmPasswordText = findViewById<View>(R.id.editTextTextPassword2) as EditText
            email = emailText.text.toString()
            password = passwordText.text.toString()
            confirmPassword = confirmPasswordText.text.toString()
            if (password != confirmPassword) {
                val invalidCred = findViewById<TextView>(R.id.textView3)
                invalidCred.text = "\"Confirm Password\" does not match \"Password\"."
                return@OnClickListener
            }
            try {
                MainActivity.am.startSignUp(email, password)
            } catch (e: Exception) {
                MainActivity.am.nullify()
                Log.i("Authentication", e.toString())
                val invalidCred = findViewById<TextView>(R.id.textView3)
                invalidCred.text = "Invalid credentials. Please try again."
                return@OnClickListener
            }
            val codeView = layoutInflater.inflate(R.layout.activity_code, null)
            val builder = AlertDialog.Builder(this@SignupPage)
            builder.setView(codeView)
            builder.setTitle("Verification code")
            builder.setMessage("Please enter the 6-digit verification code sent to your email.")
            builder.setPositiveButton("Submit") { dialog, which ->
                val codeText = codeView.findViewById<View>(R.id.editTextCode) as EditText
                val code = codeText.text.toString()
                try {
                    MainActivity.am.confirmSignUp(code)
                    MainActivity.am.signIn(email, password)
                    val intent = Intent(this@SignupPage, MainActivity::class.java)
                    intent.putExtra("prev", "Sign up")
                    startActivity(intent)
                } catch (e: Exception) {
                    MainActivity.am.nullify()
                    Log.i("Authentication", e.toString())
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which -> }
            val dialog = builder.create()
            dialog.show()
        })
    }
}