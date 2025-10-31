package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var tvCurrentLanguage: android.widget.TextView
    private lateinit var languageOption: android.widget.LinearLayout
    private lateinit var editProfileOption: android.widget.LinearLayout
    private lateinit var changePasswordOption: android.widget.LinearLayout
    private lateinit var tvAppVersion: android.widget.TextView
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sharedPreferences: SharedPreferences
    private var isThemeChanging = false // Bandera para evitar bucles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicar el tema desde las preferencias ANTES de setContentView
        applyThemeFromPreferences()

        setContentView(R.layout.activity_settings)

        initViews()
        setupSharedPreferences()
        setupListeners()
        loadCurrentSettings()
        setupAppVersion()
        setupBackPressedHandler()

        Log.d("SETTINGS_DEBUG", "✅ SettingsActivity creada correctamente")
    }

    private fun initViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage)
        languageOption = findViewById(R.id.languageOption)
        editProfileOption = findViewById(R.id.editProfileOption)
        changePasswordOption = findViewById(R.id.changePasswordOption)
        tvAppVersion = findViewById(R.id.tvAppVersion)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
    }

    private fun setupListeners() {
        // Modo Oscuro - CORREGIDO: Evitar bucle
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Si ya estamos cambiando el tema, ignorar
            if (isThemeChanging) return@setOnCheckedChangeListener

            Log.d("SETTINGS_DEBUG", "🔄 Modo oscuro cambiado a: $isChecked")

            // Guardar preferencia primero
            saveSetting("dark_mode", isChecked)

            // Activar bandera para evitar bucles
            isThemeChanging = true

            val nightMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            // Aplicar el modo inmediatamente
            AppCompatDelegate.setDefaultNightMode(nightMode)

            // Mensaje informativo
            val message = if (isChecked) {
                "Modo oscuro activado"
            } else {
                "Modo claro activado"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // NO recrear la actividad inmediatamente - dejar que AppCompatDelegate maneje el cambio
            // El sistema recreará automáticamente la actividad si es necesario
        }

        // Notificaciones
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
            }
            saveSetting("notifications", isChecked)
        }

        // Idioma
        languageOption.setOnClickListener {
            showLanguageDialog()
        }

        // Editar Perfil
        editProfileOption.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        // Cambiar Contraseña
        changePasswordOption.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de cambio de contraseña en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Botón Atrás
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English", "Português")
        val currentLanguage = sharedPreferences.getString("language", "Español")

        android.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar idioma")
            .setSingleChoiceItems(languages, languages.indexOf(currentLanguage)) { dialog, which ->
                val selectedLanguage = languages[which]
                tvCurrentLanguage.text = selectedLanguage
                saveSetting("language", selectedLanguage)
                Toast.makeText(this, "Idioma cambiado a: $selectedLanguage", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadCurrentSettings() {
        // Cargar modo oscuro desde preferencias directamente
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
        Log.d("SETTINGS_DEBUG", "📱 Modo oscuro cargado: $isDarkMode")

        // Notificaciones
        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        notificationsSwitch.isChecked = notificationsEnabled

        // Idioma
        val currentLanguage = sharedPreferences.getString("language", "Español")
        tvCurrentLanguage.text = currentLanguage
    }

    private fun setupAppVersion() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            tvAppVersion.text = "Versión $versionName"
        } catch (e: Exception) {
            tvAppVersion.text = "Versión 1.0.0"
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun saveSetting(key: String, value: Any) {
        val editor = sharedPreferences.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
        Log.d("SETTINGS_DEBUG", "💾 Configuración guardada: $key = $value")
    }

    // CORREGIDO: Aplicar tema sin forzar recreación
    private fun applyThemeFromPreferences() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)

        // Solo aplicar si es diferente al actual
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val shouldBeDark = isDarkMode
        val isCurrentlyDark = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        if (shouldBeDark != isCurrentlyDark) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.d("SETTINGS_DEBUG", "🌙 Tema oscuro aplicado desde preferencias")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d("SETTINGS_DEBUG", "☀️ Tema claro aplicado desde preferencias")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resetear la bandera cuando la actividad se reanuda
        isThemeChanging = false
        logCurrentTheme()
    }

    override fun onPause() {
        super.onPause()
        // Resetear la bandera cuando la actividad se pausa
        isThemeChanging = false
    }

    // Para debugging del tema actual
    private fun logCurrentTheme() {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val themeInfo = when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> "MODO OSCURO ACTIVO"
            AppCompatDelegate.MODE_NIGHT_NO -> "MODO CLARO ACTIVO"
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "SIGUIENDO SISTEMA"
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> "AUTO POR BATERÍA"
            else -> "DESCONOCIDO"
        }
        Log.d("SETTINGS_DEBUG", "🎨 Tema actual: $themeInfo")
    }

    // ELIMINADO: recreateWithDelay() - Ya no es necesario
}