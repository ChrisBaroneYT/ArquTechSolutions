package com.example.myapplicationarturocashfaster

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        // Aplicar el idioma guardado al contexto base de la aplicación
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializaciones adicionales si las necesitas
    }
}