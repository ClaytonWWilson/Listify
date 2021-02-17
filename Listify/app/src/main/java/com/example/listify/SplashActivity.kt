package com.example.listify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onBackPressed() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler().postDelayed({
            showSplash = false
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra("prev", "Splash")
            startActivity(intent)
        }, 3000)
    }

    companion object {
        @JvmField
        var showSplash = true
    }
}