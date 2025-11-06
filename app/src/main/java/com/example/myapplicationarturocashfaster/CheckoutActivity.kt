package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : BaseActivity() {

    private lateinit var tvOrderSummary: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etCVV: EditText
    private lateinit var etCardHolder: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCity: EditText
    private lateinit var etZipCode: EditText
    private lateinit var btnProcessPayment: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutPaymentForm: LinearLayout

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initViews()
        setupOrderSummary()
        setupListeners()
    }

    private fun initViews() {
        tvOrderSummary = findViewById(R.id.tvOrderSummary)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        etCardNumber = findViewById(R.id.etCardNumber)
        etExpiryDate = findViewById(R.id.etExpiryDate)
        etCVV = findViewById(R.id.etCVV)
        etCardHolder = findViewById(R.id.etCardHolder)
        etAddress = findViewById(R.id.etAddress)
        etCity = findViewById(R.id.etCity)
        etZipCode = findViewById(R.id.etZipCode)
        btnProcessPayment = findViewById(R.id.btnProcessPayment)
        progressBar = findViewById(R.id.progressBar)
        layoutPaymentForm = findViewById(R.id.layoutPaymentForm)
    }

    private fun setupOrderSummary() {
        val summary = CartManager.getCartSummary(this)
        val orderSummary = buildString {
            append("${getString(R.string.order_summary_title)}\n\n")
            summary.items.forEach { item ->
                val product = item.product
                val price = if (product.isForRent && product.rentPricePerDay != null) {
                    product.rentPricePerDay
                } else {
                    product.price
                }
                val total = price * item.quantity
                val type = if (product.isForRent) " (${getString(R.string.rental_suffix)})" else ""
                append("• ${product.name}$type\n")
                append("  ${getString(R.string.quantity)}: ${item.quantity} x $${String.format("%.2f", price)} = $${String.format("%.2f", total)}\n\n")
            }
            append("${getString(R.string.subtotal)} $${String.format("%.2f", summary.subtotal)}\n")
            append("${getString(R.string.tax_iva)} $${String.format("%.2f", summary.iva)}\n")
            append("${getString(R.string.total)} $${String.format("%.2f", summary.total)}")
        }

        tvOrderSummary.text = orderSummary
        tvTotalAmount.text = "$${String.format("%.2f", summary.total)}"
    }

    private fun setupListeners() {
        btnProcessPayment.setOnClickListener {
            if (validateForm()) {
                processPayment()
            }
        }

        // Formatear número de tarjeta
        etCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                formatCardNumber()
            }
        }

        // Formatear fecha de expiración
        etExpiryDate.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                formatExpiryDate()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (etCardNumber.text.toString().trim().length < 16) {
            etCardNumber.error = getString(R.string.error_invalid_card)
            return false
        }

        if (etExpiryDate.text.toString().trim().length < 5) {
            etExpiryDate.error = getString(R.string.error_invalid_expiry)
            return false
        }

        if (etCVV.text.toString().trim().length < 3) {
            etCVV.error = getString(R.string.error_invalid_cvv)
            return false
        }

        if (etCardHolder.text.toString().trim().isEmpty()) {
            etCardHolder.error = getString(R.string.error_empty_card_holder)
            return false
        }

        if (etAddress.text.toString().trim().isEmpty()) {
            etAddress.error = getString(R.string.error_empty_address)
            return false
        }

        if (etCity.text.toString().trim().isEmpty()) {
            etCity.error = getString(R.string.error_empty_city)
            return false
        }

        if (etZipCode.text.toString().trim().isEmpty()) {
            etZipCode.error = getString(R.string.error_empty_zip_code)
            return false
        }

        return true
    }

    private fun formatCardNumber() {
        val text = etCardNumber.text.toString().replace(" ", "")
        if (text.length >= 16) {
            val formatted = text.chunked(4).joinToString(" ")
            etCardNumber.setText(formatted)
        }
    }

    private fun formatExpiryDate() {
        val text = etExpiryDate.text.toString().replace("/", "")
        if (text.length >= 4) {
            val formatted = "${text.substring(0, 2)}/${text.substring(2, 4)}"
            etExpiryDate.setText(formatted)
        }
    }

    private fun processPayment() {
        val paymentData = PaymentData(
            cardNumber = etCardNumber.text.toString().replace(" ", ""),
            expiryDate = etExpiryDate.text.toString(),
            cvv = etCVV.text.toString(),
            cardHolder = etCardHolder.text.toString(),
            amount = CartManager.getCartSummary(this).total
        )

        // Mostrar loading
        progressBar.visibility = View.VISIBLE
        layoutPaymentForm.alpha = 0.5f
        btnProcessPayment.isEnabled = false

        scope.launch {
            try {
                val result = ApiService.processPayment(paymentData)

                progressBar.visibility = View.GONE
                layoutPaymentForm.alpha = 1f
                btnProcessPayment.isEnabled = true

                if (result.success) {
                    showPaymentSuccess()
                } else {
                    showPaymentError(result.message)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                layoutPaymentForm.alpha = 1f
                btnProcessPayment.isEnabled = true
                showPaymentError("${getString(R.string.connection_error)}: ${e.message}")
            }
        }
    }

    // 🔄 NUEVA FUNCIÓN: Guardar pedido en el historial
    private fun saveOrder(orderNumber: String) {
        val cartItems = CartManager.getCartItems(this)
        val cartSummary = CartManager.getCartSummary(this)

        val order = Order(
            id = orderNumber,
            date = Date(),
            items = cartItems,
            total = cartSummary.total,
            status = OrderStatus.PROCESSING,
            shippingAddress = "${etAddress.text}, ${etCity.text} ${etZipCode.text}",
            paymentMethod = "${getString(R.string.card_ending)} ${etCardNumber.text.toString().takeLast(4)}"
        )

        OrderManager.addOrder(order)
    }

    private fun showPaymentSuccess() {
        // Generar número de orden
        val orderNumber = "ORD-${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())}"

        // 🔄 ACTUALIZADO: Guardar el pedido en el historial antes de limpiar el carrito
        saveOrder(orderNumber)

        // Limpiar carrito
        CartManager.clearCart(this)

        val intent = Intent(this, PaymentSuccessActivity::class.java).apply {
            putExtra("ORDER_NUMBER", orderNumber)
            putExtra("TOTAL_AMOUNT", CartManager.getCartSummary(this@CheckoutActivity).total)
        }
        startActivity(intent)
        finish()
    }

    private fun showPaymentError(message: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.payment_error_title))
            .setMessage("${getString(R.string.payment_error_message)}: $message")
            .setPositiveButton(getString(R.string.retry), null)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}