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

    // Arrays para los idiomas
    private val languageCodes = arrayOf("es", "en", "pt")
    private val languageNames = arrayOf(
        "Español",
        "English",
        "Português"
    )

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
        Log.d("LANGUAGE_DEBUG", "Idioma actual: ${getCurrentLanguageCode()} - ${getCurrentLanguageName()}")
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
        val currentLanguageCode = getCurrentLanguageCode()
        val currentIndex = languageCodes.indexOf(currentLanguageCode)

        val dialogLanguageNames = arrayOf(
            getString(R.string.spanish),
            getString(R.string.english),
            getString(R.string.portuguese)
        )

        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(dialogLanguageNames, currentIndex) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]
                val selectedLanguageName = languageNames[which]

                Log.d("LANGUAGE_DEBUG", "Idioma seleccionado: $selectedLanguageCode - $selectedLanguageName")

                changeAppLanguage(selectedLanguageCode, selectedLanguageName)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun changeAppLanguage(languageCode: String, languageName: String) {
        // Verificar si el idioma es diferente al actual
        if (languageCode == getCurrentLanguageCode()) {
            Log.d("LANGUAGE_DEBUG", "El idioma ya está establecido: $languageCode")
            return
        }

        Log.d("LANGUAGE_DEBUG", "Cambiando idioma a: $languageCode")

        // Guardar la preferencia de idioma
        saveSetting("language", languageCode)

        // Aplicar el nuevo idioma usando LocaleHelper
        LocaleHelper.setLocale(this, languageCode)

        // Mostrar mensaje
        val message = "${getString(R.string.language_changed_to)}: $languageName"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        // Reiniciar TODA la aplicación para aplicar el idioma completamente
        restartApp()
    }

    private fun loadCurrentSettings() {
        // Cargar modo oscuro
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
        Log.d("SETTINGS_DEBUG", "📱 Modo oscuro cargado: $isDarkMode")

        // Notificaciones
        val notificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        notificationsSwitch.isChecked = notificationsEnabled

        // Idioma - Usar el helper para obtener el nombre correcto
        val currentLanguageName = getCurrentLanguageName()
        tvCurrentLanguage.text = currentLanguageName
        Log.d("LANGUAGE_DEBUG", "Idioma cargado en UI: $currentLanguageName")
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

        // Actualizar el idioma en caso de que haya cambiado
        val currentLanguageName = getCurrentLanguageName()
        tvCurrentLanguage.text = currentLanguageName
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