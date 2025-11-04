package com.example.myapplicationarturocashfaster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderNumber.text = "Orden #${order.id}"
        holder.tvOrderDate.text = order.getFormattedDate()
        holder.tvOrderTotal.text = order.getFormattedTotal()
        holder.tvOrderStatus.text = getStatusText(order.status)

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount() = orders.size

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