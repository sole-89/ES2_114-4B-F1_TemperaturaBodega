package com.example.miprimeraaplicacion2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Redirige a BienvenidaActivity en lugar de MainActivity
            startActivity(Intent(this, BienvenidaActivity::class.java))
            finish()
        }, 2000)
    }
}
