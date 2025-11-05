package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvError: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupSharedPreferences()
        checkSharedPreferences() // NUEVO: Verificar SharedPreferences
        setupListeners()

        Log.d("LOGIN_DEBUG", "✅ LoginActivity creada")
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvError = findViewById(R.id.tvError)
    }

    private fun setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
    }

    // NUEVO: Función para verificar SharedPreferences
    private fun checkSharedPreferences() {
        val allEntries = sharedPreferences.all

        Log.d("SHARED_PREFS_DEBUG", "📊 [LOGIN] Contenido de SharedPreferences:")
        for ((key, value) in allEntries) {
            Log.d("SHARED_PREFS_DEBUG", "   $key = $value")
        }

        if (allEntries.isEmpty()) {
            Log.d("SHARED_PREFS_DEBUG", "   ⚠️ SharedPreferences está VACÍO")
        }
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        Log.d("LOGIN_DEBUG", "🔵 Iniciando login...")
        Log.d("LOGIN_DEBUG", "   📝 Email: $email")
        Log.d("LOGIN_DEBUG", "   📝 Password: ${if (password.isNotEmpty()) "***" + password.length + "***" else "VACÍA"}")

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_fields))
            Log.e("LOGIN_DEBUG", "❌ Campos vacíos")
            return
        }

        if (!isValidEmail(email)) {
            showError(getString(R.string.error_invalid_email))
            Log.e("LOGIN_DEBUG", "❌ Email inválido: $email")
            return
        }

        Log.d("LOGIN_DEBUG", "✅ Validaciones pasadas, iniciando login...")

        btnLogin.isEnabled = false
        btnLogin.text = getString(R.string.logging_in)

        scope.launch {
            try {
                Log.d("LOGIN_DEBUG", "🔵 Llamando a SupabaseManager.loginUser()")

                val result = SupabaseManager.loginUser(email, password)
                val success = result.first
                val message = result.second

                Log.d("LOGIN_DEBUG", "🔵 Resultado login: success=$success, message=$message")

                btnLogin.isEnabled = true
                btnLogin.text = getString(R.string.login_button)

                if (success) {
                    Log.d("LOGIN_DEBUG", "✅ Login exitoso, guardando sesión...")
                    showSuccess(getString(R.string.login_success))
                    val username = email.substringBefore("@")
                    saveUserSession(username, email)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("LOGIN_DEBUG", "❌ Error en login: $message")
                    showError(getString(R.string.login_error, message))
                }
            } catch (e: Exception) {
                Log.e("LOGIN_DEBUG", "🔴 Error en corrutina de login: ${e.message}", e)
                btnLogin.isEnabled = true
                btnLogin.text = getString(R.string.login_button)
                showError(getString(R.string.generic_error, e.message ?: getString(R.string.unknown_error)))
            }
        }
    }

    private fun saveUserSession(username: String, email: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putLong("login_time", System.currentTimeMillis())
        val success = editor.commit() // Usamos commit() para saber si se guardó

        Log.d("LOGIN_DEBUG", "💾 Guardando sesión - success=$success")
        Log.d("LOGIN_DEBUG", "   👤 Username: $username")
        Log.d("LOGIN_DEBUG", "   📧 Email: $email")

        if (success) {
            Log.d("LOGIN_DEBUG", "✅ Sesión guardada exitosamente")
        } else {
            Log.e("LOGIN_DEBUG", "❌ Error al guardar sesión")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = TextView.VISIBLE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        tvError.visibility = TextView.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}