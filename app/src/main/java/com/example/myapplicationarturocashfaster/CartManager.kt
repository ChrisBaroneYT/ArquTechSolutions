package com.example.myapplicationarturocashfaster

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val CART_PREFS = "cart_preferences"
    private const val CART_ITEMS_KEY = "cart_items"
    private const val IVA_PERCENTAGE = 0.16

    private val gson = Gson()

    fun addToCart(context: Context, product: Product, quantity: Int = 1) {
        val cartItems = getCartItems(context).toMutableList()
        val existingItem = cartItems.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(product, quantity))
        }

        saveCartItems(context, cartItems)
    }

    fun removeFromCart(context: Context, productId: Int) {
        val cartItems = getCartItems(context).toMutableList()
        cartItems.removeAll { it.product.id == productId }
        saveCartItems(context, cartItems)
    }

    fun updateQuantity(context: Context, productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(context, productId)
            return
        }

        val cartItems = getCartItems(context).toMutableList()
        val item = cartItems.find { it.product.id == productId }
        item?.quantity = quantity
        saveCartItems(context, cartItems)
    }

    fun getCartItems(context: Context): List<CartItem> {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(CART_ITEMS_KEY, null)

        return if (json != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun clearCart(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(CART_ITEMS_KEY).apply()
    }

    fun getCartSummary(context: Context): CartSummary {
        val cartItems = getCartItems(context)
        val subtotal = cartItems.sumOf {
            if (it.product.isForRent && it.product.rentPricePerDay != null) {
                it.product.rentPricePerDay * it.quantity
            } else {
                it.product.price * it.quantity
            }
        }
        val iva = subtotal * IVA_PERCENTAGE
        val total = subtotal + iva

        return CartSummary(subtotal, iva, total, cartItems)
    }

    fun getCartItemsCount(context: Context): Int {
        return getCartItems(context).sumOf { it.quantity }
    }

    private fun saveCartItems(context: Context, items: List<CartItem>) {
        val sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
        val json = gson.toJson(items)
        sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply()
    }
}

data class CartItem(val product: Product, var quantity: Int)
data class CartSummary(val subtotal: Double, val iva: Double, val total: Double, val items: List<CartItem>)