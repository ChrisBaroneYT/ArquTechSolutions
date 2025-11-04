package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplicationarturocashfaster.adapters.SliderAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var dots: Array<ImageView?>

    // Variables para navegación y usuario
    private lateinit var btnNavServices: Button
    private lateinit var btnNavBookings: Button
    private lateinit var btnNavProfile: Button
    private lateinit var btnNavStore: Button // NUEVO: Botón para tienda
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var layoutNotLoggedIn: LinearLayout
    private lateinit var layoutLoggedIn: LinearLayout
    private lateinit var tvWelcomeUser: TextView
    private lateinit var btnLogoutMain: Button

    private val sliderImages = intArrayOf(
        R.drawable.slider1,           // Tu imagen 1 del slider
        R.drawable.slider1_interior,  // Tu imagen 2 del slider
        R.drawable.slider2,           // Tu imagen 3 del slider
        R.drawable.slider2_interior,  // Tu imagen 4 del slider
        R.drawable.slider3,           // Tu imagen 5 del slider
        R.drawable.slider3_interior,  // Tu imagen 6 del slider
        R.drawable.slider4,           // Tu imagen 7 del slider
        R.drawable.slider4_interior,  // Tu imagen 8 del slider
    )

    private var currentPage = 0
    private val sliderDelay: Long = 2000 // 2 segundos
    private val handler = Handler(Looper.getMainLooper())

    // Variable para controlar si el slider está activo
    private var isSliderActive = true

    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (isSliderActive) {
                currentPage = (currentPage + 1) % sliderImages.size
                viewPagerSlider.setCurrentItem(currentPage, true)
                handler.postDelayed(this, sliderDelay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        initViews()
        setupSlider()
        setupClickListeners()
        setupNavigation() // Nueva función de navegación
        updateUserInterface() // Actualizar interfaz según estado de usuario

        // Iniciar el slider automático
        startAutoSlider()
    }

    private fun initViews() {
        viewPagerSlider = findViewById(R.id.viewPagerSlider)
        layoutDots = findViewById(R.id.layoutDots)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun setupSlider() {
        sliderAdapter = SliderAdapter(sliderImages.toList())
        viewPagerSlider.adapter = sliderAdapter
        setupDots()

        viewPagerSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                setCurrentDot(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        pauseAutoSlider() // Pausar cuando el usuario interactúa
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        // Reanudar después de 2 segundos de inactividad
                        handler.postDelayed({
                            startAutoSlider()
                        }, sliderDelay)
                    }
                }
            }
        })
    }

    // FUNCIÓN: Iniciar slider automático
    private fun startAutoSlider() {
        isSliderActive = true
        handler.removeCallbacks(sliderRunnable) // Limpiar cualquier callback previo
        handler.postDelayed(sliderRunnable, sliderDelay)
    }

    // FUNCIÓN: Pausar slider automático
    private fun pauseAutoSlider() {
        isSliderActive = false
        handler.removeCallbacks(sliderRunnable)
    }

    private fun setupDots() {
        dots = arrayOfNulls(sliderImages.size)
        layoutDots.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.dot_inactive)
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            layoutDots.addView(dots[i], params)
        }

        setCurrentDot(0)
    }

    private fun setCurrentDot(position: Int) {
        for (i in dots.indices) {
            val drawable = if (i == position) {
                R.drawable.dot_active
            } else {
                R.drawable.dot_inactive
            }
            dots[i]?.setImageResource(drawable)
        }
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // NUEVA FUNCIÓN: Configurar navegación
    private fun setupNavigation() {
        btnNavServices = findViewById(R.id.btnNavServices)
        btnNavBookings = findViewById(R.id.btnNavBookings)
        btnNavProfile = findViewById(R.id.btnNavProfile)
        btnNavStore = findViewById(R.id.btnNavStore) // NUEVO: Inicializar botón tienda
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Inicializar vistas de usuario
        layoutNotLoggedIn = findViewById(R.id.layoutNotLoggedIn)
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn)
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        btnLogoutMain = findViewById(R.id.btnLogoutMain)

        btnNavServices.setOnClickListener {
            navigateToServiceDetail()
        }

        btnNavBookings.setOnClickListener {
            navigateToBookings()
        }

        btnNavProfile.setOnClickListener {
            navigateToProfile()
        }

        // NUEVO: Navegación a la tienda
        btnNavStore.setOnClickListener {
            navigateToStore()
        }
    }

    // NUEVAS FUNCIONES DE NAVEGACIÓN
    private fun navigateToServiceDetail() {
        val intent = Intent(this, ServiceDetailActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBookings() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        } else {
            // Si no está logueado, ir al login primero
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToProfile() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        } else {
            // Si no está logueado, ir al login primero
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // NUEVA FUNCIÓN: Navegación a la tienda
    private fun navigateToStore() {
        val intent = Intent(this, StoreActivity::class.java)
        startActivity(intent)
    }

    // NUEVA FUNCIÓN PARA ACTUALIZAR INTERFAZ DE USUARIO
    private fun updateUserInterface() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val username = sharedPreferences.getString("username", "")

        if (isLoggedIn && !username.isNullOrEmpty()) {
            // Usuario logueado - mostrar bienvenida
            val formattedUsername = username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            layoutNotLoggedIn.visibility = View.GONE
            layoutLoggedIn.visibility = View.VISIBLE
            tvWelcomeUser.text = "¡Hola, $formattedUsername!"

            // Configurar logout
            btnLogoutMain.setOnClickListener {
                showLogoutConfirmation()
            }
        } else {
            // Usuario no logueado - mostrar login/registro
            layoutNotLoggedIn.visibility = View.VISIBLE
            layoutLoggedIn.visibility = View.GONE
        }
    }

    // FUNCIÓN PARA CERRAR SESIÓN
    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.remove("is_logged_in")
        editor.remove("username")
        editor.remove("email")
        editor.apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        updateUserInterface() // Actualizar la interfaz
    }

    override fun onResume() {
        super.onResume()
        startAutoSlider()
        updateUserInterface() // Actualizar interfaz al volver
    }

    override fun onPause() {
        super.onPause()
        pauseAutoSlider()
    }

    override fun onDestroy() {
        super.onDestroy()
        pauseAutoSlider()
    }
}