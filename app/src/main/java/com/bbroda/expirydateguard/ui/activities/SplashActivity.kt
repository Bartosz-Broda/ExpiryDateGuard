package com.bbroda.expirydateguard.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import org.greenrobot.eventbus.EventBus.TAG
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d(TAG, "onCreate: SPLASH SCREEN ON")

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(setAppLocale(getlanguageFromSharedPref()), MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

        Log.d(TAG, "onCreate: SPLASH SCREEN ON")
    }

    private fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    private fun getlanguageFromSharedPref():String{
        try{
            val sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang","")
            return if (language.isNullOrBlank()){
                ""
            } else{
                Log.d(TAG, "getlanguageFromSharedPref: $language")
                return language
            }
        }catch (e:Exception){
            Log.d(TAG, "getlanguageFromSharedPref: EXCEPTION: $e")
            return ""
        }
    }


}