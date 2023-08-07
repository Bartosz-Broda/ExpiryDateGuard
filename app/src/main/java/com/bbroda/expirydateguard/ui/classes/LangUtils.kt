
package com.bbroda.expirydateguard.ui.classes
/*
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat.isAtLeastT
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.bbroda.expirydateguard.R
import org.greenrobot.eventbus.EventBus
import java.util.*

object LangUtils {
    const val LANG_AUTO = "auto"
    const val LANG_DEFAULT = "en"
    private var sLocaleMap: ArrayMap<String, Locale?>? = null
    fun setAppLanguages(context: Context) {
        if (sLocaleMap == null) sLocaleMap = ArrayMap()
        val res = context.resources
        val conf = res.configuration
        // Assume that there is an array called language_key which contains all the supported language tags
        val locales = context.resources.getStringArray(R.array.languages_key)
        val appDefaultLocale = Locale.forLanguageTag(LANG_DEFAULT)
        for (locale in locales) {
            conf.setLocale(Locale.forLanguageTag(locale))
            val ctx = context.createConfigurationContext(conf)
            val langTag = ctx.getString(R.string._lang_tag)
            if (LANG_AUTO == locale) {
                sLocaleMap!![LANG_AUTO] = null
            } else if (LANG_DEFAULT == langTag) {
                sLocaleMap!![LANG_DEFAULT] = appDefaultLocale
            } else sLocaleMap!![locale] = ConfigurationCompat.getLocales(conf)[0]
        }
    }

    fun getAppLanguages(context: Context): ArrayMap<String, Locale?> {
        if (sLocaleMap == null) setAppLanguages(context)
        return sLocaleMap!!
    }

    fun getFromPreference(context: Context): Locale {
        var locale: Locale
        if (Build.VERSION.SDK_INT >= 33) {
            locale = AppCompatDelegate.getApplicationLocales().getFirstMatch(
                getAppLanguages(context).keys
                    .toTypedArray()
            )!!
            if (locale != null) {
                return locale
            }
        }
        // Fall-back to shared preferences
        try{
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang","")
            return if (language.isNullOrBlank()){
                getAppLanguages(context)[language]!!
            } else{
                Log.d(EventBus.TAG, "getlanguageFromSharedPref: $language")
                return Locale.forLanguageTag(language)
            }
        }catch (e:Exception){
            Log.d(EventBus.TAG, "getlanguageFromSharedPref: EXCEPTION: $e")
            // Load from system configuration
            val conf = Resources.getSystem().configuration
            return conf.locales[0]
        }
    }

    private fun applyLocale(context: Context): Locale {
        return applyLocale(context, getFromPreference(context))
    }

    fun applyLocale(context: Context, locale: Locale): Locale {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
        updateResources(context.applicationContext, locale)
        return locale
    }

    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val res = context.resources
        var conf = res.configuration
        val current =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) conf.locales[0] else conf.locale
        if (current === locale) {
            return
        }
        conf = Configuration(conf)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleApi24(conf, locale)
        } else {
            conf.setLocale(locale)
        }
        res.updateConfiguration(conf, res.displayMetrics)
    }

    private fun setLocaleApi24(config: Configuration, locale: Locale) {
        val defaultLocales = LocaleList.getDefault()
        val locales = LinkedHashSet<Locale>(defaultLocales.size() + 1)
        // Bring the target locale to the front of the list
        // There's a hidden API, but it's not currently used here.
        locales.add(locale)
        for (i in 0 until defaultLocales.size()) {
            locales.add(defaultLocales[i])
        }
        config.setLocales(LocaleList(*locales.toTypedArray()))
    }
}*/
