package com.bbroda.expirydateguard.ui.classes

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale


class MyContextWrapper(base: Context?) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, language: String): ContextWrapper {
            var context = context
            val config = context.resources.configuration
            var sysLocale: Locale? = null
            sysLocale =
                getSystemLocale(config)
            if (language != "" && sysLocale.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                setSystemLocale(config, locale)
            }
            context = context.createConfigurationContext(config)
            return MyContextWrapper(context)
        }
        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales[0]
        }
        private fun setSystemLocale(config: Configuration, locale: Locale?) {
            config.setLocale(locale)
        }
    }
}