package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ServiceDetailActivity : BaseActivity() {

    // IDs para el primer servicio (Diseño)
    private lateinit var serviceImage: ImageView
    private lateinit var serviceName: TextView
    private lateinit var serviceDescription: TextView
    private lateinit var servicePrice: TextView

    // IDs para el segundo servicio (Planos)
    private lateinit var servicePlanosImage: ImageView
    private lateinit var servicePlanosName: TextView
    private lateinit var servicePlanosDescription: TextView
    private lateinit var servicePlanosPrice: TextView

    // IDs para el tercer servicio (Supervisión)
    private lateinit var serviceSupervisionImage: ImageView
    private lateinit var serviceSupervisionName: TextView
    private lateinit var serviceSupervisionDescription: TextView
    private lateinit var serviceSupervisionPrice: TextView

    // Botón para volver al inicio
    private lateinit var btnBackToHome: Button

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        initViews()
        setupListeners()
        setupBackPressedHandler()
    }

    private fun initViews() {
        // Servicio 1: Diseño
        serviceImage = findViewById(R.id.service_detail_image)
        serviceName = findViewById(R.id.service_detail_name)
        serviceDescription = findViewById(R.id.service_detail_description)
        servicePrice = findViewById(R.id.service_detail_price)

        // Servicio 2: Planos
        servicePlanosImage = findViewById(R.id.service_planos_image)
        servicePlanosName = findViewById(R.id.service_planos_name)
        servicePlanosDescription = findViewById(R.id.service_planos_description)
        servicePlanosPrice = findViewById(R.id.service_planos_price)

        // Servicio 3: Supervisión
        serviceSupervisionImage = findViewById(R.id.service_supervision_image)
        serviceSupervisionName = findViewById(R.id.service_supervision_name)
        serviceSupervisionDescription = findViewById(R.id.service_supervision_description)
        serviceSupervisionPrice = findViewById(R.id.service_supervision_price)

        // Botón Volver al Inicio
        btnBackToHome = findViewById(R.id.btnBackToHome)
    }

    private fun setupListeners() {
        // Botón para volver al inicio
        btnBackToHome.setOnClickListener {
            goBackToHome()
        }
    }

    private fun setupBackPressedHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBackToHome()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun goBackToHome() {
        // Navegar de vuelta a la actividad principal (MainActivity)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}