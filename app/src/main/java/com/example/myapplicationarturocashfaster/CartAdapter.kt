package com.example.myapplicationarturocashfaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val totalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val btnDecrease: MaterialButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: MaterialButton = itemView.findViewById(R.id.btnIncrease)
        val btnRemove: MaterialButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product = cartItem.product

        // Usar imagen por defecto
        holder.productImage.setImageResource(R.drawable.ing_maria)

        holder.productName.text = product.name

        // Mostrar precio según si es renta o venta
        val unitPrice = if (product.isForRent && product.rentPricePerDay != null) {
            product.rentPricePerDay
        } else {
            product.price
        }

        val priceType = if (product.isForRent) "día" else "unidad"
        holder.productPrice.text = "$${String.format("%.2f", unitPrice)}/$priceType"

        holder.productQuantity.text = cartItem.quantity.toString()

        // Calcular precio total para este item
        val itemTotal = unitPrice * cartItem.quantity
        holder.totalPrice.text = "$${String.format("%.2f", itemTotal)}"

        // Listeners para botones de cantidad
        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                onQuantityChanged(product.id, cartItem.quantity - 1)
            }
        }

        holder.btnIncrease.setOnClickListener {
            if (cartItem.quantity < product.stock) {
                onQuantityChanged(product.id, cartItem.quantity + 1)
            } else {
                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "No hay más stock disponible",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.btnRemove.setOnClickListener {
            onRemoveItem(product.id)
        }
    }

    override fun getItemCount() = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        this.cartItems = newItems
        notifyDataSetChanged()
    }
}