package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton // ✅ Import correcto
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StoreActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var tvCartCount: TextView
    private lateinit var btnViewCart: ImageButton // ✅ CORREGIDO: ImageButton en lugar de Button
    private lateinit var btnCategories: Button
    private lateinit var btnTools: Button
    private lateinit var btnMaterials: Button
    private lateinit var btnRental: Button

    private val allProducts = mutableListOf<Product>()
    private var filteredProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        initViews()
        setupProducts()
        setupRecyclerView()
        updateCartUI()
    }

    private fun initViews() {
        rvProducts = findViewById(R.id.rvProducts)
        tvCartCount = findViewById(R.id.tvCartCount)
        btnViewCart = findViewById(R.id.btnViewCart) // ✅ Ahora funciona correctamente
        btnCategories = findViewById(R.id.btnCategories)
        btnTools = findViewById(R.id.btnTools)
        btnMaterials = findViewById(R.id.btnMaterials)
        btnRental = findViewById(R.id.btnRental)

        btnViewCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnCategories.setOnClickListener {
            showCategoriesDialog()
        }

        btnTools.setOnClickListener {
            filterProductsByCategory(ProductCategory.HERRAMIENTAS)
        }

        btnMaterials.setOnClickListener {
            filterProductsByCategory(ProductCategory.MATERIALES)
        }

        btnRental.setOnClickListener {
            filterProductsByCategory(ProductCategory.ALQUILER)
        }
    }

    private fun setupProducts() {
        // Productos de ferretería/construcción
        allProducts.addAll(
            listOf(
                Product(1, "🛠️ Martillo Profesional", "Martillo de acero forjado con mango ergonómico", 25.99, R.drawable.martillo, ProductCategory.HERRAMIENTAS, stock = 15),
                Product(2, "⚡ Taladro Percutor 18V", "Taladro inalámbrico con 2 baterías y maletín", 89.99, R.drawable.taladro, ProductCategory.HERRAMIENTAS, true, 15.00, 8),
                Product(3, "🏗️ Cemento Gris 50kg", "Cemento para construcción general", 12.50, R.drawable.cemento, ProductCategory.MATERIALES, stock = 100),
                Product(4, "🧱 Ladrillos x100", "Ladrillos cerámicos estándar 12x24cm", 45.00, R.drawable.ladrillos, ProductCategory.MATERIALES, stock = 500),
                Product(5, "🪜 Andamio Profesional", "Andamio tubular 2x1.5m para construcción", 0.0, R.drawable.andamio, ProductCategory.ALQUILER, true, 35.00, 10),
                Product(6, "🌀 Mezcladora 180L", "Mezcladora eléctrica para concreto", 0.0, R.drawable.mezcladora, ProductCategory.ALQUILER, true, 50.00, 5),
                Product(7, "🦺 Chaleco Reflectante", "Chaleco de seguridad alta visibilidad", 8.99, R.drawable.chaleco, ProductCategory.SEGURIDAD, stock = 30),
                Product(8, "🚜 Carretilla Industrial", "Carretilla de acero capacidad 100L", 67.50, R.drawable.carretilla, ProductCategory.EQUIPOS, stock = 12),
                Product(9, "🔌 Cable Eléctrico 2.5mm", "Cable THHN 2.5mm x 100m", 85.00, R.drawable.cable, ProductCategory.ELECTRICIDAD, stock = 25),
                Product(10, "🚰 Tubo PVC 1/2\"", "Tubo PVC para fontanería x 6m", 8.75, R.drawable.pvc, ProductCategory.FONTANERIA, stock = 80),
                Product(11, "📏 Nivel Láser", "Nivel láser automático con trípode", 45.00, R.drawable.laser, ProductCategory.HERRAMIENTAS, true, 12.00, 6),
                Product(12, "🪚 Sierra Circular", "Sierra circular profesional 1200W", 120.00, R.drawable.sierra, ProductCategory.HERRAMIENTAS, true, 25.00, 4)
            )
        )
        filteredProducts.addAll(allProducts)
    }

    private fun setupRecyclerView() {
        val adapter = ProductAdapter(filteredProducts,
            onAddToCart = { product ->
                CartManager.addToCart(this, product)
                updateCartUI()
                showAddedToCartMessage(product.name)
            },
            onProductClick = { product ->
                showProductDetail(product)
            }
        )

        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = adapter
    }

    private fun showProductDetail(product: Product) {
        android.widget.Toast.makeText(this, "Detalle de: ${product.name}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun filterProductsByCategory(category: ProductCategory) {
        filteredProducts.clear()
        filteredProducts.addAll(allProducts.filter { it.category == category })
        rvProducts.adapter?.notifyDataSetChanged()
    }

    private fun updateCartUI() {
        val cartItemsCount = CartManager.getCartItemsCount(this)
        tvCartCount.text = cartItemsCount.toString()
        tvCartCount.visibility = if (cartItemsCount > 0) View.VISIBLE else View.INVISIBLE
    }

    private fun showAddedToCartMessage(productName: String) {
        android.widget.Toast.makeText(this, "✅ $productName añadido al carrito", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showCategoriesDialog() {
        val categories = ProductCategory.values().map {
            when (it) {
                ProductCategory.HERRAMIENTAS -> "🛠️ Herramientas"
                ProductCategory.MATERIALES -> "🏗️ Materiales"
                ProductCategory.EQUIPOS -> "🔧 Equipos"
                ProductCategory.SEGURIDAD -> "🦺 Seguridad"
                ProductCategory.ALQUILER -> "📦 Alquiler"
                ProductCategory.ELECTRICIDAD -> "⚡ Electricidad"
                ProductCategory.FONTANERIA -> "🚰 Fontanería"
            }
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("📂 Categorías")
            .setItems(categories.toTypedArray()) { dialog, which ->
                val selectedCategory = ProductCategory.values()[which]
                filterProductsByCategory(selectedCategory)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateCartUI()
    }
}