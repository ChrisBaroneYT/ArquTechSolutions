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
        // Modo Oscuro - MEJORADO con recreación de actividad
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SETTINGS_DEBUG", "🔄 Modo oscuro cambiado a: $isChecked")

            val nightMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            // Aplicar el modo inmediatamente
            AppCompatDelegate.setDefaultNightMode(nightMode)

            // Guardar preferencia
            saveSetting("dark_mode", isChecked)

            // Mensaje informativo
            val message = if (isChecked) {
                "Modo oscuro activado - Reiniciando aplicación..."
            } else {
                "Modo claro activado - Reiniciando aplicación..."
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // Reiniciar la actividad para aplicar cambios inmediatamente
            recreateWithDelay()
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
        // Modo Oscuro - MEJORADO con detección más precisa
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val isDarkMode = when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                // Verificar si el sistema está en modo oscuro
                val currentSystemMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                currentSystemMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
            else -> false
        }

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
        // ✅ CORREGIDO: Usar la nueva API sin warnings
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

    // NUEVO: Aplicar tema desde preferencias
    private fun applyThemeFromPreferences() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Log.d("SETTINGS_DEBUG", "🌙 Tema oscuro aplicado desde preferencias")
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Log.d("SETTINGS_DEBUG", "☀️ Tema claro aplicado desde preferencias")
        }
    }

    // NUEVO: Reiniciar actividad con delay para mejor UX
    private fun recreateWithDelay() {
        // Pequeño delay para que el usuario vea el Toast
        btnBack.postDelayed({
            recreate()
        }, 500)
    }

    // NUEVO: Para debugging del tema actual
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

    override fun onResume() {
        super.onResume()
        logCurrentTheme() // Debug del tema al volver
    }

    // ✅ ELIMINADO: onBackPressed() deprecated - Ya no es necesario
    // El callback del dispatcher maneja todo
}