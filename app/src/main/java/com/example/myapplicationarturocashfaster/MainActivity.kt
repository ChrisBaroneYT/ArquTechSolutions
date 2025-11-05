package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

class MainActivity : BaseActivity() {

    private lateinit var viewPagerSlider: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var dots: Array<ImageView?>

    // NUEVAS VARIABLES PARA SLIDER DE PRODUCTOS
    private lateinit var viewPagerStore: ViewPager2
    private lateinit var layoutStoreDots: LinearLayout
    private lateinit var storeSliderAdapter: SliderAdapter
    private lateinit var storeDots: Array<ImageView?>

    // Variables para navegación y usuario
    private lateinit var btnNavServices: Button
    private lateinit var btnNavBookings: Button
    private lateinit var btnNavProfile: Button
    private lateinit var btnNavStore: Button
    private lateinit var btnNavSettings: Button // ✅ DECLARADO
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var layoutNotLoggedIn: LinearLayout
    private lateinit var layoutLoggedIn: LinearLayout
    private lateinit var tvWelcomeUser: TextView
    private lateinit var btnLogoutMain: Button

    // Slider de Proyectos (original)
    private val sliderImages = intArrayOf(
        R.drawable.slider1,
        R.drawable.slider1_interior,
        R.drawable.slider2,
        R.drawable.slider2_interior,
        R.drawable.slider3,
        R.drawable.slider3_interior,
        R.drawable.slider4,
        R.drawable.slider4_interior,
    )

    // NUEVO: Slider de Productos Destacados
    private val storeImages = intArrayOf(
        R.drawable.taladro,
        R.drawable.martillo,
        R.drawable.sierra,
        R.drawable.laser
    )

    private var currentPage = 0
    private var currentStorePage = 0
    private val sliderDelay: Long = 2000
    private val handler = Handler(Looper.getMainLooper())

    private var isSliderActive = true
    private var isStoreSliderActive = true

    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (isSliderActive) {
                currentPage = (currentPage + 1) % sliderImages.size
                viewPagerSlider.setCurrentItem(currentPage, true)
                handler.postDelayed(this, sliderDelay)
            }
        }
    }

    // NUEVO: Runnable para slider de productos
    private val storeSliderRunnable = object : Runnable {
        override fun run() {
            if (isStoreSliderActive) {
                currentStorePage = (currentStorePage + 1) % storeImages.size
                viewPagerStore.setCurrentItem(currentStorePage, true)
                handler.postDelayed(this, sliderDelay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleHelper.onAttach(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        initViews()
        setupSlider()
        setupStoreSlider()
        setupClickListeners()
        setupNavigation()
        updateUserInterface()

        startAutoSlider()
        startStoreAutoSlider()
    }

    private fun initViews() {
        viewPagerSlider = findViewById(R.id.viewPagerSlider)
        layoutDots = findViewById(R.id.layoutDots)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // NUEVO: Inicializar vistas del slider de productos
        viewPagerStore = findViewById(R.id.viewPagerStore)
        layoutStoreDots = findViewById(R.id.layoutStoreDots)
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
                        pauseAutoSlider()
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        handler.postDelayed({
                            startAutoSlider()
                        }, sliderDelay)
                    }
                }
            }
        })
    }

    // NUEVA FUNCIÓN: Configurar slider de productos
    private fun setupStoreSlider() {
        storeSliderAdapter = SliderAdapter(storeImages.toList())
        viewPagerStore.adapter = storeSliderAdapter
        setupStoreDots()

        viewPagerStore.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentStorePage = position
                setCurrentStoreDot(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        pauseStoreAutoSlider()
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        handler.postDelayed({
                            startStoreAutoSlider()
                        }, sliderDelay)
                    }
                }
            }
        })
    }

    private fun startAutoSlider() {
        isSliderActive = true
        handler.removeCallbacks(sliderRunnable)
        handler.postDelayed(sliderRunnable, sliderDelay)
    }

    private fun pauseAutoSlider() {
        isSliderActive = false
        handler.removeCallbacks(sliderRunnable)
    }

    // NUEVAS FUNCIONES PARA SLIDER DE PRODUCTOS
    private fun startStoreAutoSlider() {
        isStoreSliderActive = true
        handler.removeCallbacks(storeSliderRunnable)
        handler.postDelayed(storeSliderRunnable, sliderDelay)
    }

    private fun pauseStoreAutoSlider() {
        isStoreSliderActive = false
        handler.removeCallbacks(storeSliderRunnable)
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

    // NUEVA FUNCIÓN: Dots para productos
    private fun setupStoreDots() {
        storeDots = arrayOfNulls(storeImages.size)
        layoutStoreDots.removeAllViews()

        for (i in storeDots.indices) {
            storeDots[i] = ImageView(this)
            storeDots[i]?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.dot_inactive)
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            layoutStoreDots.addView(storeDots[i], params)
        }
        setCurrentStoreDot(0)
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

    // NUEVA FUNCIÓN: Cambiar dot activo para productos
    private fun setCurrentStoreDot(position: Int) {
        for (i in storeDots.indices) {
            val drawable = if (i == position) {
                R.drawable.dot_active
            } else {
                R.drawable.dot_inactive
            }
            storeDots[i]?.setImageResource(drawable)
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

    private fun setupNavigation() {
        btnNavServices = findViewById(R.id.btnNavServices)
        btnNavBookings = findViewById(R.id.btnNavBookings)
        btnNavProfile = findViewById(R.id.btnNavProfile)
        btnNavStore = findViewById(R.id.btnNavStore)
        btnNavSettings = findViewById(R.id.btnNavSettings) // ✅ INICIALIZADO
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

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

        btnNavStore.setOnClickListener {
            navigateToStore()
        }

        // ✅ AGREGAR ESTE LISTENER - ES LO QUE FALTABA
        btnNavSettings.setOnClickListener {
            navigateToSettings()
        }
    }

    private fun navigateToServiceDetail() {
        val intent = Intent(this, ServiceDetailActivity::class.java)
        startActivity(intent)
    }

    // ✅ FUNCIÓN PARA AJUSTES
    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBookings() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        } else {
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToStore() {
        val intent = Intent(this, StoreActivity::class.java)
        startActivity(intent)
    }

    private fun updateUserInterface() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val username = sharedPreferences.getString("username", "")

        if (isLoggedIn && !username.isNullOrEmpty()) {
            val formattedUsername = username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            layoutNotLoggedIn.visibility = View.GONE
            layoutLoggedIn.visibility = View.VISIBLE
            tvWelcomeUser.text = "¡Hola, $formattedUsername!"

            btnLogoutMain.setOnClickListener {
                showLogoutConfirmation()
            }
        } else {
            layoutNotLoggedIn.visibility = View.VISIBLE
            layoutLoggedIn.visibility = View.GONE
        }
    }

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
        updateUserInterface()
    }

    override fun onResume() {
        super.onResume()
        startAutoSlider()
        startStoreAutoSlider()
        updateUserInterface()
    }

    override fun onPause() {
        super.onPause()
        pauseAutoSlider()
        pauseStoreAutoSlider()
    }

    override fun onDestroy() {
        super.onDestroy()
        pauseAutoSlider()
        pauseStoreAutoSlider()
        handler.removeCallbacks(sliderRunnable)
        handler.removeCallbacks(storeSliderRunnable)
    }
}