package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import java.util.*

class UserProfileActivity : BaseActivity() {

    private lateinit var tvDashboardWelcome: TextView
    private lateinit var tvDashboardUserName: TextView
    private lateinit var tvDashboardUserEmail: TextView
    private lateinit var btnLogout: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    // Variables para navegación - ACTUALIZADAS
    private lateinit var cardServices: CardView
    private lateinit var cardContact: CardView
    private lateinit var cardHome: CardView
    private lateinit var cardSettings: CardView
    private lateinit var ivUserProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        try {
            initViews()
            setupSharedPreferences()
            setupUserInfo()
            setupListeners()
            setupNavigationCards()
            loadUserProfileImage()

            // ✅ DEBUG: Verificar que cardSettings se inicializó
            if (::cardSettings.isInitialized) {
                Log.d("DEBUG", "✅ cardSettings inicializado correctamente")
            } else {
                Log.e("DEBUG", "❌ cardSettings NO se inicializó")
            }

            Log.d("DEBUG", "✅ UserProfile cargado exitosamente")

        } catch (e: Exception) {
            Log.e("DEBUG", "❌ ERROR: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        tvDashboardWelcome = findViewById(R.id.tvDashboardWelcome)
        tvDashboardUserName = findViewById(R.id.tvDashboardUserName)
        tvDashboardUserEmail = findViewById(R.id.tvDashboardUserEmail)
        btnLogout = findViewById(R.id.btnLogout)

        // Inicializar vistas de navegación - ACTUALIZADAS
        ivUserProfile = findViewById(R.id.ivUserProfile)
        cardServices = findViewById(R.id.cardServices)
        cardContact = findViewById(R.id.cardContact)
        cardHome = findViewById(R.id.cardHome)
        cardSettings = findViewById(R.id.cardSettings)

        // ❌ ELIMINADOS: Estos IDs ya no existen en el XML
        // tvBookingsCount = findViewById(R.id.tvBookingsCount)
        // tvServicesCount = findViewById(R.id.tvServicesCount)
        // btnBookService = findViewById(R.id.btnBookService)
        // btnMyBookings = findViewById(R.id.btnMyBookings)
        // recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView)
        // cardBookings = findViewById(R.id.cardBookings)
        // cardProfile = findViewById(R.id.cardProfile)
    }

    private fun setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
    }

    private fun setupUserInfo() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        if (isLoggedIn && !username.isNullOrEmpty()) {
            // Usuario logueado - mostrar información personalizada
            val formattedUsername = username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            tvDashboardUserName.text = formattedUsername
            tvDashboardUserEmail.text = email
            tvDashboardWelcome.text = getString(R.string.welcome_back, formattedUsername)

            // ❌ ELIMINADO: Estadísticas ya no se muestran
            // tvBookingsCount.text = "2"
            // tvServicesCount.text = "5"

        } else {
            // Usuario no logueado - redirigir a login
            Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfileImage() {
        // Imagen de perfil genérica
        val profileImageUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80"

        Glide.with(this)
            .load(profileImageUrl)
            .placeholder(android.R.drawable.ic_menu_myplaces)
            .error(android.R.drawable.ic_menu_myplaces)
            .circleCrop()
            .into(ivUserProfile)
    }

    private fun setupListeners() {
        // ❌ ELIMINADOS: Botones que ya no existen
        // btnBookService.setOnClickListener {
        //     val intent = Intent(this, ServiceDetailActivity::class.java)
        //     startActivity(intent)
        // }
        //
        // btnMyBookings.setOnClickListener {
        //     val intent = Intent(this, ContactActivity::class.java)
        //     startActivity(intent)
        // }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    // ✅ ACTUALIZADO: Configurar tarjetas de navegación (CONFIGURACIÓN FUNCIONAL)
    private fun setupNavigationCards() {
        cardServices.setOnClickListener {
            Log.d("DEBUG", "🎯 Botón Servicios presionado")
            val intent = Intent(this, ServiceDetailActivity::class.java)
            startActivity(intent)
        }

        cardContact.setOnClickListener {
            Log.d("DEBUG", "🎯 Botón Contacto presionado")
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        cardHome.setOnClickListener {
            Log.d("DEBUG", "🎯 Botón Inicio presionado")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        cardSettings.setOnClickListener {
            // ✅ CONFIGURACIÓN COMPLETAMENTE FUNCIONAL
            Log.d("DEBUG", "🎯 Botón Configuración presionado - Abriendo SettingsActivity")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        ivUserProfile.setOnClickListener {
            Toast.makeText(this, "Ya estás en tu perfil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Eliminar los datos de sesión del usuario
        editor.remove("is_logged_in")
        editor.remove("username")
        editor.remove("email")
        editor.remove("login_time")
        editor.apply()

        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

        // Redirigir al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        setupUserInfo() // Actualizar info al volver
    }
}