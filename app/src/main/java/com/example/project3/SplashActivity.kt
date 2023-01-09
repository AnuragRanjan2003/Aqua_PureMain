package com.example.project3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // proceed on permissions provided
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000L)
            proceed()
        }


    }

    private fun onPermissionsProvided() {}

    private fun proceed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}