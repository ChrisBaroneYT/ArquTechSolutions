package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : BaseActivity() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIVA: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmptyCart: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnContinueShopping: Button
    private lateinit var layoutCartSummary: View

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupRecyclerView()
        updateCartSummary()
        setupListeners()
    }

    private fun initViews() {
        rvCartItems = findViewById(R.id.rvCartItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIVA = findViewById(R.id.tvIVA)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmptyCart = findViewById(R.id.tvEmptyCart)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnContinueShopping = findViewById(R.id.btnContinueShopping)
        layoutCartSummary = findViewById(R.id.layoutCartSummary)
    }

    private fun setupRecyclerView() {
        val cartItems = CartManager.getCartItems(this)

        cartAdapter = CartAdapter(cartItems,
            onQuantityChanged = { productId, newQuantity ->
                CartManager.updateQuantity(this, productId, newQuantity)
                updateCartSummary()
            },
            onRemoveItem = { productId ->
                CartManager.removeFromCart(this, productId)
                updateCartSummary()
                // Actualizar el adaptador
                val updatedItems = CartManager.getCartItems(this)
                cartAdapter.updateItems(updatedItems)
                checkEmptyCart()
            }
        )

        rvCartItems.layoutManager = LinearLayoutManager(this)
        rvCartItems.adapter = cartAdapter

        checkEmptyCart()
    }

    private fun setupListeners() {
        btnContinueShopping.setOnClickListener {
            finish()
        }

        btnCheckout.setOnClickListener {
            val cartSummary = CartManager.getCartSummary(this)
            if (cartSummary.items.isNotEmpty()) {
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            } else {
                android.widget.Toast.makeText(this, "El carrito está vacío", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCartSummary() {
        val summary = CartManager.getCartSummary(this)

        tvSubtotal.text = "Subtotal: $${String.format("%.2f", summary.subtotal)}"
        tvIVA.text = "IVA (16%): $${String.format("%.2f", summary.iva)}"
        tvTotal.text = "Total: $${String.format("%.2f", summary.total)}"

        btnCheckout.isEnabled = summary.items.isNotEmpty()
    }

    private fun checkEmptyCart() {
        val cartItems = CartManager.getCartItems(this)
        val isEmpty = cartItems.isEmpty()

        if (isEmpty) {
            tvEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            layoutCartSummary.visibility = View.GONE
            btnCheckout.visibility = View.GONE
        } else {
            tvEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            layoutCartSummary.visibility = View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartSummary()
        checkEmptyCart()
    }
}