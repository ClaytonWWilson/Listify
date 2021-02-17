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

class ForgotPasswordPage : AppCompatActivity() {
    private var button1: Button? = null
    var email: String? = null
    var newPassword: String? = null
    var confirmNewPassword: String? = null
    override fun onBackPressed() {
        val prev = intent.getStringExtra("prev")
        if (prev != null && (prev == "Sign up" || prev == "Log in")) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpswd)
        button1 = findViewById<View>(R.id.button1) as Button
        button1!!.setOnClickListener(View.OnClickListener {
            val emailText = findViewById<View>(R.id.editTextTextEmailAddress) as EditText
            val newPasswordText = findViewById<View>(R.id.editTextTextPassword) as EditText
            val confirmNewPasswordText = findViewById<View>(R.id.editTextTextPassword2) as EditText
            email = emailText.text.toString()
            newPassword = newPasswordText.text.toString()
            confirmNewPassword = confirmNewPasswordText.text.toString()
            if (newPassword != confirmNewPassword) {
                val invalidCred = findViewById<TextView>(R.id.textView6)
                invalidCred.text = "\"Confirm New Password\" does not match \"New Password\"."
                return@OnClickListener
            }
            try {
                MainActivity.am.changePassword(email)
            } catch (e: Exception) {
                MainActivity.am.nullify()
                Log.i("Authentication", e.toString())
                val invalidCred = findViewById<TextView>(R.id.textView6)
                invalidCred.text = "Password criteria not met. Please try again."
                return@OnClickListener
            }
            val codeView = layoutInflater.inflate(R.layout.activity_code, null)
            val builder = AlertDialog.Builder(this@ForgotPasswordPage)
            builder.setView(codeView)
            builder.setTitle("Verification code")
            builder.setMessage("Please enter the 6-digit verification code sent to your email.")
            builder.setPositiveButton("Submit") { dialog, which ->
                val codeText = codeView.findViewById<View>(R.id.editTextCode) as EditText
                val code = codeText.text.toString()
                try {
                    MainActivity.am.confirmPasswordReset(newPassword, code)
                    val intent = Intent(this@ForgotPasswordPage, LoginPage::class.java)
                    intent.putExtra("prev", "Forgot password")
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