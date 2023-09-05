package com.bbroda.expirydateguard.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import org.greenrobot.eventbus.EventBus.TAG
import java.util.*

@SuppressLint("CustomSplashScreen")
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        Log.d(TAG, "onCreate: About Activity ON")

    }

}