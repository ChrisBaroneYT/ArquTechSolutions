package com.example.myapplicationarturocashfaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val rentPrice: TextView = itemView.findViewById(R.id.tvRentPrice)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
        val stockIndicator: TextView = itemView.findViewById(R.id.tvStock)
        val productCard: View = itemView.findViewById(R.id.productCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Usar imagen por defecto (ing_maria.png) para todos los productos por ahora
        holder.productImage.setImageResource(product.image)

        holder.productName.text = product.name
        holder.productDescription.text = product.description

        if (product.isForRent) {
            holder.productPrice.text = "${getString(holder.itemView.context, R.string.sale)}: $${product.price}"
            holder.rentPrice.text = "${getString(holder.itemView.context, R.string.rent)}: $${product.rentPricePerDay}/${getString(holder.itemView.context, R.string.day)}"
            holder.rentPrice.visibility = View.VISIBLE
            holder.btnAddToCart.text = getString(holder.itemView.context, R.string.rent_button)
        } else {
            holder.productPrice.text = "$${product.price}"
            holder.rentPrice.visibility = View.GONE
            holder.btnAddToCart.text = getString(holder.itemView.context, R.string.buy_button)
        }

        // Indicador de stock
        when {
            product.stock == 0 -> {
                holder.stockIndicator.text = getString(holder.itemView.context, R.string.out_of_stock)
                holder.stockIndicator.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                holder.btnAddToCart.isEnabled = false
                holder.btnAddToCart.alpha = 0.5f
            }
            product.stock < 5 -> {
                holder.stockIndicator.text = "${getString(holder.itemView.context, R.string.last_units)} ${product.stock}"
                holder.stockIndicator.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange))
                holder.btnAddToCart.isEnabled = true
                holder.btnAddToCart.alpha = 1f
            }
            else -> {
                holder.stockIndicator.text = "${getString(holder.itemView.context, R.string.stock)}: ${product.stock}"
                holder.stockIndicator.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
                holder.btnAddToCart.isEnabled = true
                holder.btnAddToCart.alpha = 1f
            }
        }

        holder.btnAddToCart.setOnClickListener {
            onAddToCart(product)
        }

        holder.productCard.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount() = products.size

    private fun getString(context: android.content.Context, resId: Int): String {
        return context.getString(resId)
    }
}