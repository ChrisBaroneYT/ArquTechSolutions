package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : BaseActivity() {

    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var tvCurrentLanguage: android.widget.TextView
    private lateinit var languageOption: android.widget.LinearLayout
    private lateinit var editProfileOption: android.widget.LinearLayout
    private lateinit var changePasswordOption: android.widget.LinearLayout
    private lateinit var tvAppVersion: android.widget.TextView
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sharedPreferences: SharedPreferences
    private var isThemeChanging = false

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
        // Modo Oscuro
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isThemeChanging) return@setOnCheckedChangeListener

            Log.d("SETTINGS_DEBUG", "🔄 Modo oscuro cambiado a: $isChecked")
            saveSetting("dark_mode", isChecked)
            isThemeChanging = true

            val nightMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.setDefaultNightMode(nightMode)

            val message = if (isChecked) {
                getString(R.string.dark_mode_enabled)
            } else {
                getString(R.string.light_mode_enabled)
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Notificaciones
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) {
                getString(R.string.notifications_enabled)
            } else {
                getString(R.string.notifications_disabled)
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, getString(R.string.change_password_development), Toast.LENGTH_SHORT).show()
        }

        // Botón Atrás
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.spanish),
            getString(R.string.english),
            getString(R.string.portuguese)
        )
        val currentLanguageCode = LocaleHelper.getPersistedLocale(this)
        val currentLanguageName = LocaleHelper.getLanguageName(currentLanguageCode)

        val currentIndex = languages.indexOf(currentLanguageName)

        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                val selectedLanguageName = languages[which]
                val selectedLanguageCode = LocaleHelper.getLanguageCode(selectedLanguageName)

                // Cambiar el idioma inmediatamente
                changeAppLanguage(selectedLanguageCode, selectedLanguageName)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun changeAppLanguage(languageCode: String, languageName: String) {
        // Guardar la preferencia de idioma
        saveSetting("language", languageCode)

        // Aplicar el nuevo idioma usando LocaleHelper
        LocaleHelper.setLocale(this, languageCode)

        // Actualizar la interfaz inmediatamente
        updateUIWithNewLanguage(languageName)

        // Mostrar mensaje
        Toast.makeText(this, "${getString(R.string.language_changed_to)}: $languageName", Toast.LENGTH_SHORT).show()

        // Reiniciar TODA la aplicación para aplicar el idioma completamente
        restartApp()
    }

    // Función para actualizar la UI inmediatamente después del cambio de idioma
    private fun updateUIWithNewLanguage(languageName: String) {
        tvCurrentLanguage.text = languageName

        // También puedes actualizar otros textos si es necesario
        // Por ejemplo, el título de la actividad
        supportActionBar?.title = getString(R.string.settings)
    }

    // Función para reiniciar toda la aplicación
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity() // Cierra todas las actividades
    }

    private fun loadCurrentSettings() {
        // Cargar modo oscuro
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
        Log.d("SETTINGS_DEBUG", "📱 Modo oscuro cargado: $isDarkMode")

        // Notificaciones
        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        notificationsSwitch.isChecked = notificationsEnabled

        // Idioma
        val currentLanguageCode = LocaleHelper.getPersistedLocale(this)
        val currentLanguageName = LocaleHelper.getLanguageName(currentLanguageCode)
        tvCurrentLanguage.text = currentLanguageName
    }

    private fun setupAppVersion() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            tvAppVersion.text = "${getString(R.string.version)} $versionName"
        } catch (e: Exception) {
            tvAppVersion.text = "${getString(R.string.version)} 1.0.0"
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

    private fun applyThemeFromPreferences() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)

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
        isThemeChanging = false
        logCurrentTheme()
    }

    override fun onPause() {
        super.onPause()
        isThemeChanging = false
    }

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
}