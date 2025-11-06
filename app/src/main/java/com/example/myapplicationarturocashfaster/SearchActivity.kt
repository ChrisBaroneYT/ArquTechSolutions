package com.example.myapplicationarturocashfaster

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : BaseActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnClear: ImageButton
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var searchAdapter: ProductAdapter

    private val allProducts = mutableListOf<Product>()
    private var searchResults = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupProducts()
        setupRecyclerView()
        setupSearchListener()
        setupBackPressedHandler()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        btnBack = findViewById(R.id.btnBack)
        btnClear = findViewById(R.id.btnClear)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        btnBack.setOnClickListener {
            finish()
        }

        btnClear.setOnClickListener {
            etSearch.text.clear()
        }

        // Enfocar el campo de búsqueda automáticamente
        etSearch.requestFocus()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupProducts() {
        // Usar los mismos productos que en StoreActivity
        allProducts.addAll(
            listOf(
                Product(1, "🛠️ Martillo Profesional", "Martillo de acero forjado con mango ergonómico", 25.99, R.drawable.ing_maria, ProductCategory.HERRAMIENTAS, stock = 15),
                Product(2, "⚡ Taladro Percutor 18V", "Taladro inalámbrico con 2 baterías y maletín", 89.99, R.drawable.ing_maria, ProductCategory.HERRAMIENTAS, true, 15.00, 8),
                Product(3, "🏗️ Cemento Gris 50kg", "Cemento para construcción general", 12.50, R.drawable.ing_maria, ProductCategory.MATERIALES, stock = 100),
                Product(4, "🧱 Ladrillos x100", "Ladrillos cerámicos estándar 12x24cm", 45.00, R.drawable.ing_maria, ProductCategory.MATERIALES, stock = 500),
                Product(5, "🪜 Andamio Profesional", "Andamio tubular 2x1.5m para construcción", 0.0, R.drawable.ing_maria, ProductCategory.ALQUILER, true, 35.00, 10),
                Product(6, "🌀 Mezcladora 180L", "Mezcladora eléctrica para concreto", 0.0, R.drawable.ing_maria, ProductCategory.ALQUILER, true, 50.00, 5),
                Product(7, "🦺 Chaleco Reflectante", "Chaleco de seguridad alta visibilidad", 8.99, R.drawable.ing_maria, ProductCategory.SEGURIDAD, stock = 30),
                Product(8, "🚜 Carretilla Industrial", "Carretilla de acero capacidad 100L", 67.50, R.drawable.ing_maria, ProductCategory.EQUIPOS, stock = 12),
                Product(9, "🔌 Cable Eléctrico 2.5mm", "Cable THHN 2.5mm x 100m", 85.00, R.drawable.ing_maria, ProductCategory.ELECTRICIDAD, stock = 25),
                Product(10, "🚰 Tubo PVC 1/2\"", "Tubo PVC para fontanería x 6m", 8.75, R.drawable.ing_maria, ProductCategory.FONTANERIA, stock = 80),
                Product(11, "📏 Nivel Láser", "Nivel láser automático con trípode", 45.00, R.drawable.ing_maria, ProductCategory.HERRAMIENTAS, true, 12.00, 6),
                Product(12, "🪚 Sierra Circular", "Sierra circular profesional 1200W", 120.00, R.drawable.ing_maria, ProductCategory.HERRAMIENTAS, true, 25.00, 4)
            )
        )
        searchResults.addAll(allProducts)
    }

    private fun setupRecyclerView() {
        searchAdapter = ProductAdapter(searchResults,
            onAddToCart = { product ->
                CartManager.addToCart(this, product)
                showAddedToCartMessage(product.name)
            },
            onProductClick = { product ->
                // Por ahora mostramos un toast
                showProductDetail(product)
            }
        )

        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        rvSearchResults.adapter = searchAdapter
    }

    private fun showProductDetail(product: Product) {
        android.widget.Toast.makeText(this, "${getString(R.string.product_detail)}: ${product.name}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClear.visibility = if (s.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            searchResults.clear()
            searchResults.addAll(allProducts)
        } else {
            searchResults.clear()
            val lowerQuery = query.lowercase()
            searchResults.addAll(allProducts.filter {
                it.name.lowercase().contains(lowerQuery) ||
                        it.description.lowercase().contains(lowerQuery) ||
                        it.category.name.lowercase().contains(lowerQuery)
            })
        }
        searchAdapter.notifyDataSetChanged()
    }

    private fun showAddedToCartMessage(productName: String) {
        android.widget.Toast.makeText(this, "✅ $productName ${getString(R.string.added_to_cart)}", android.widget.Toast.LENGTH_SHORT).show()
    }
}