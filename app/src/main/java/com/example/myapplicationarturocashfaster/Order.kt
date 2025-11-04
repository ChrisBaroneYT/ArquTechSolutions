package com.example.myapplicationarturocashfaster

import java.text.SimpleDateFormat
import java.util.*

data class Order(
    val id: String,
    val date: Date,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val shippingAddress: String,
    val paymentMethod: String
) {
    fun getFormattedDate(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
    }

    fun getFormattedTotal(): String {
        return "$${String.format("%.2f", total)}"
    }
}

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

object OrderManager {
    private val orders = mutableListOf<Order>()

    fun addOrder(order: Order) {
        orders.add(0, order)
    }

    fun getOrders(): List<Order> {
        return orders.toList()
    }

    fun getOrderById(orderId: String): Order? {
        return orders.find { it.id == orderId }
    }

    fun clearOrders() {
        orders.clear()
    }
}