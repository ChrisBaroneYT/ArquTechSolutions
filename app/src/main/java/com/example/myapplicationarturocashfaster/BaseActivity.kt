package com.example.myapplicationarturocashfaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Aplicar el idioma guardado al contexto base
        val context = LocaleHelper.onAttach(newBase)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asegurar que el título se actualice con el idioma correcto
        supportActionBar?.setTitle(R.string.app_name)
    }

    protected fun getCurrentLanguageName(): String {
        val currentLanguageCode = LocaleHelper.getPersistedLocale(this)
        return LocaleHelper.getLanguageName(currentLanguageCode)
    }

    protected fun getCurrentLanguageCode(): String {
        return LocaleHelper.getPersistedLocale(this)
    }

    protected fun changeAppLanguage(languageCode: String) {
        LocaleHelper.setLocale(this, languageCode)
        recreate() // Reiniciar activity para aplicar cambios
    }

    protected fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity()
    }
}