package com.example.myapplicationarturocashfaster

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderDetailActivity : BaseActivity() {

    private lateinit var tvOrderNumber: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvShippingAddress: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIVA: TextView
    private lateinit var tvTotal: TextView
    private lateinit var rvOrderItems: RecyclerView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initViews()
        setupBackPressedHandler() // ✅ NUEVO: Manejo moderno de back pressed
        loadOrderData()
    }

    private fun initViews() {
        tvOrderNumber = findViewById(R.id.tvOrderNumber)
        tvOrderDate = findViewById(R.id.tvOrderDate)
        tvOrderStatus = findViewById(R.id.tvOrderStatus)
        tvShippingAddress = findViewById(R.id.tvShippingAddress)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIVA = findViewById(R.id.tvIVA)
        tvTotal = findViewById(R.id.tvTotal)
        rvOrderItems = findViewById(R.id.rvOrderItems)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish() // ✅ CORREGIDO: Usar finish() en lugar de onBackPressed()
        }
    }

    // ✅ NUEVO: Manejo moderno del botón back
    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun loadOrderData() {
        val orderId = intent.getStringExtra("ORDER_ID")
        val order = OrderManager.getOrderById(orderId ?: "")

        if (order != null) {
            tvOrderNumber.text = "Orden #${order.id}"
            tvOrderDate.text = order.getFormattedDate()
            tvOrderStatus.text = getStatusText(order.status)
            tvShippingAddress.text = order.shippingAddress
            tvPaymentMethod.text = order.paymentMethod

            // Calcular totales
            val subtotal = order.items.sumOf { item ->
                val price = if (item.product.isForRent && item.product.rentPricePerDay != null) {
                    item.product.rentPricePerDay
                } else {
                    item.product.price
                }
                price * item.quantity
            }
            val iva = subtotal * 0.16
            val total = subtotal + iva

            tvSubtotal.text = "Subtotal: $${String.format("%.2f", subtotal)}"
            tvIVA.text = "IVA (16%): $${String.format("%.2f", iva)}"
            tvTotal.text = "Total: $${String.format("%.2f", total)}"

            // Configurar lista de items
            val adapter = OrderItemsAdapter(order.items)
            rvOrderItems.layoutManager = LinearLayoutManager(this)
            rvOrderItems.adapter = adapter
        }
    }

    private fun getStatusText(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "⏳ Pendiente"
            OrderStatus.PROCESSING -> "🔄 Procesando"
            OrderStatus.SHIPPED -> "🚚 Enviado"
            OrderStatus.DELIVERED -> "✅ Entregado"
            OrderStatus.CANCELLED -> "❌ Cancelado"
        }
    }
}