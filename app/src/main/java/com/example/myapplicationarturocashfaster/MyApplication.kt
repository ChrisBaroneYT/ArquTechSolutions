package com.example.myapplicationarturocashfaster

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializar cualquier configuración global aquí si es necesario
    }

    override fun attachBaseContext(base: Context) {
        // Aplicar el idioma guardado al contexto base
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}