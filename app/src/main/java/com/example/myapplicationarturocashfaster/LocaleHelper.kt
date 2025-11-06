package com.example.myapplicationarturocashfaster

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "app_language"

    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return updateResourcesLegacy(context, language)
    }

    private fun persist(context: Context, language: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    fun getPersistedLocale(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, "es") ?: "es"
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = getLocaleForLanguage(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @SuppressWarnings("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = getLocaleForLanguage(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }

    private fun getLocaleForLanguage(language: String): Locale {
        return when (language) {
            "en" -> Locale.ENGLISH
            "pt" -> Locale("pt", "BR") // Portugués Brasil
            else -> Locale("es") // Español por defecto
        }
    }

    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "en" -> "English"
            "pt" -> "Português"
            else -> "Español"
        }
    }

    fun getLanguageCode(languageName: String): String {
        return when (languageName) {
            "English" -> "en"
            "Português" -> "pt"
            "Español" -> "es"
            else -> "es"
        }
    }

    fun onAttach(context: Context): Context {
        val lang = getPersistedLocale(context)
        return setLocale(context, lang)
    }
}