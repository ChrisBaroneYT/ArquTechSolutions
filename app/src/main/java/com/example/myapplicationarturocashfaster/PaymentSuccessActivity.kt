package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentSuccessActivity : BaseActivity() {

    private lateinit var tvOrderNumber: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnBackToStore: Button
    private lateinit var btnViewOrders: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        initViews()
        setupOrderInfo()
        setupListeners()
    }

    private fun initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnBackToStore = findViewById(R.id.btnBackToStore)
        btnViewOrders = findViewById(R.id.btnViewOrders)
    }

    private fun setupOrderInfo() {
        val orderNumber = intent.getStringExtra("ORDER_NUMBER") ?: "N/A"
        val totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        tvOrderNumber.text = "Orden #$orderNumber"
        tvTotalAmount.text = "Total: $${String.format("%.2f", totalAmount)}"
    }

    private fun setupListeners() {
        btnBackToStore.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnViewOrders.setOnClickListener {
            // Por ahora volvemos al store, luego podemos implementar historial de pedidos
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}