package com.example.project3

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log.e
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.project3.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import java.net.InetAddress

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // proceed on permissions provided
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000L)
            withContext(Dispatchers.Main) {
                proceedOnOK()
            }
        }

        binding.tryAgain.setOnClickListener {
            proceedOnOK()
        }


    }

    private fun proceedOnOK() {
        e("internet","${isInternetAvailable()}")
        if (isInternetAvailable()) {
            proceed()
        } else {
            binding.animInternet.visibility = View.VISIBLE
            binding.animSplash.visibility = View.INVISIBLE
            binding.tryAgain.visibility = View.VISIBLE

        }
    }

    private fun proceed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val conM =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifi = conM.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = conM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            wifi!=null && wifi.isConnected || mobile!=null && mobile.isConnected
        } catch (e: Exception) {
            false
        }
    }

}