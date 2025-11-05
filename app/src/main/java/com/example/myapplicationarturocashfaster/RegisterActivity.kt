package com.example.myapplicationarturocashfaster

import android.content.Intent
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

class RegisterActivity : BaseActivity() {

    private lateinit var etIdentification: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var tvError: TextView

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        checkSharedPreferences() // NUEVO: Verificar SharedPreferences
        setupListeners()

        Log.d("REGISTER_DEBUG", "✅ RegisterActivity creada")
    }

    private fun initViews() {
        etIdentification = findViewById(R.id.etIdentification)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        tvError = findViewById(R.id.tvError)
    }

    // NUEVO: Función para verificar SharedPreferences
    private fun checkSharedPreferences() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        Log.d("SHARED_PREFS_DEBUG", "📊 [REGISTER] Contenido de SharedPreferences:")
        for ((key, value) in allEntries) {
            Log.d("SHARED_PREFS_DEBUG", "   $key = $value")
        }

        if (allEntries.isEmpty()) {
            Log.d("SHARED_PREFS_DEBUG", "   ⚠️ SharedPreferences está VACÍO")
        }
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val identification = etIdentification.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        Log.d("REGISTER_DEBUG", "🔵 Iniciando registro...")
        Log.d("REGISTER_DEBUG", "   📝 Identificación: $identification")
        Log.d("REGISTER_DEBUG", "   📝 Username: $username")
        Log.d("REGISTER_DEBUG", "   📝 Email: $email")
        Log.d("REGISTER_DEBUG", "   📝 Password: ${if (password.isNotEmpty()) "***" + password.length + "***" else "VACÍA"}")
        Log.d("REGISTER_DEBUG", "   📝 Confirm Password: ${if (confirmPassword.isNotEmpty()) "***" + confirmPassword.length + "***" else "VACÍA"}")

        if (identification.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError(getString(R.string.error_empty_fields))
            Log.e("REGISTER_DEBUG", "❌ Campos vacíos")
            return
        }

        if (password != confirmPassword) {
            showError(getString(R.string.error_passwords_not_match))
            Log.e("REGISTER_DEBUG", "❌ Contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            showError(getString(R.string.error_password_length))
            Log.e("REGISTER_DEBUG", "❌ Contraseña muy corta: ${password.length} caracteres")
            return
        }

        if (!isValidEmail(email)) {
            showError(getString(R.string.error_invalid_email))
            Log.e("REGISTER_DEBUG", "❌ Email inválido: $email")
            return
        }

        Log.d("REGISTER_DEBUG", "✅ Validaciones pasadas, iniciando registro...")

        btnRegister.isEnabled = false
        btnRegister.text = getString(R.string.registering)

        scope.launch {
            try {
                Log.d("REGISTER_DEBUG", "🔵 Llamando a SupabaseManager.registerUser()")

                val result = SupabaseManager.registerUser(
                    identification = identification,
                    username = username,
                    email = email,
                    password = password
                )
                val success = result.first
                val message = result.second

                Log.d("REGISTER_DEBUG", "🔵 Resultado registro: success=$success, message=$message")

                btnRegister.isEnabled = true
                btnRegister.text = getString(R.string.register_button)

                if (success) {
                    Log.d("REGISTER_DEBUG", "✅ Registro exitoso, redirigiendo a login...")
                    showSuccess(getString(R.string.register_success))
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("REGISTER_DEBUG", "❌ Error en registro: $message")
                    showError(getString(R.string.register_error, message))
                }
            } catch (e: Exception) {
                Log.e("REGISTER_DEBUG", "🔴 Error en corrutina de registro: ${e.message}", e)
                btnRegister.isEnabled = true
                btnRegister.text = getString(R.string.register_button)
                showError(getString(R.string.generic_error, e.message ?: getString(R.string.unknown_error)))
            }
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