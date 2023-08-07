package com.bbroda.expirydateguard.ui.activities


import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.MyContextWrapper
import com.bbroda.expirydateguard.ui.mvp.model.MainMenuModel
import com.bbroda.expirydateguard.ui.mvp.presenter.MainMenuPresenter
import com.bbroda.expirydateguard.ui.mvp.view.MainMenuView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBus.TAG
import java.util.Locale


class MainMenuActivity : AppCompatActivity() {

    private lateinit var presenter: MainMenuPresenter
    private val bus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)

        presenter = MainMenuPresenter(
            MainMenuView(this, bus),
            MainMenuModel(bus),
            this
        )

        EventBus.getDefault().register(presenter)
        bus.post(MainMenuView.InitRecyclerView())


        //można dać opcję z sharedpref - jeśli otwarte menu boczcne to backpress robi tylko reload main menu
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Whatever you want
                // when back pressed
                println("Back button pressed")
                finish()
            }
        })

    }

    override fun attachBaseContext(newBase: Context) {
        val lang = getlanguageFromSharedPref(newBase) // at start no language selected so default is english
        super.attachBaseContext(MyContextWrapper.wrap(newBase, lang))
    }

    /*fun reloadWithNewBaseContext(lang:String){
        val intent = Intent(updateResources(applicationContext,getlanguageFromSharedPref()), SplashActivity::class.java)
        Log.d(TAG, "reloadWithNewBaseContext: lang: $lang")
        finish()
        startActivity(intent)
    }*/

    public override fun onResume() {
        super.onResume()
        if (!bus.isRegistered(presenter)){
            bus.register(presenter)
        }

        bus.post(MainMenuView.ReloadProducts())
    }

    public override fun onPause() {
        bus.unregister(presenter)
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    private fun getlanguageFromSharedPref(context: Context):String{
        try{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
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

    private fun updateResources(context: Context, language: String): Context? {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context = context.createConfigurationContext(config)
        return context
    }

}
