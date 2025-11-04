package com.example.myapplicationarturocashfaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemsAdapter(
    private val items: List<CartItem>
) : RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductTotal: TextView = itemView.findViewById(R.id.tvProductTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        val product = item.product

        val unitPrice = if (product.isForRent && product.rentPricePerDay != null) {
            product.rentPricePerDay
        } else {
            product.price
        }

        val totalPrice = unitPrice * item.quantity
        val priceType = if (product.isForRent) "día" else "unidad"

        holder.tvProductName.text = product.name
        holder.tvProductQuantity.text = "${item.quantity} x $${String.format("%.2f", unitPrice)}/$priceType"
        holder.tvProductPrice.text = "$${String.format("%.2f", unitPrice)}/$priceType"
        holder.tvProductTotal.text = "$${String.format("%.2f", totalPrice)}"
    }

    override fun getItemCount() = items.size
}