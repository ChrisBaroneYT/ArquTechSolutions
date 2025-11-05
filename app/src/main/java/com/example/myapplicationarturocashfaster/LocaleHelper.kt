package com.example.myapplicationarturocashfaster

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun onAttach(context: Context): Context {
        val locale = getPersistedLocale(context)
        return setLocale(context, locale)
    }

    fun setLocale(context: Context, language: String): Context {
        persistLocale(context, language)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }
    }

    fun getPersistedLocale(context: Context): String {
        val preferences = getSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, "es") ?: "es"
    }

    private fun persistLocale(context: Context, language: String) {
        val preferences = getSharedPreferences(context)
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "es" -> "Español"
            "en" -> "English"
            "pt" -> "Português"
            else -> "Español"
        }
    }

    fun getLanguageCode(languageName: String): String {
        return when (languageName) {
            "Español" -> "es"
            "English" -> "en"
            "Português" -> "pt"
            else -> "es"
        }
    }
}