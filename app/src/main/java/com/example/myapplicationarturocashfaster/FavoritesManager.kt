package com.example.myapplicationarturocashfaster

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesManager {
    private const val FAVORITES_PREFS = "favorites_preferences"
    private const val FAVORITES_KEY = "favorite_products"

    private val gson = Gson()

    fun addToFavorites(context: Context, product: Product) {
        val favorites = getFavorites(context).toMutableList()
        if (!favorites.any { it.id == product.id }) {
            favorites.add(product)
            saveFavorites(context, favorites)
        }
    }

    fun removeFromFavorites(context: Context, productId: Int) {
        val favorites = getFavorites(context).toMutableList()
        favorites.removeAll { it.id == productId }
        saveFavorites(context, favorites)
    }

    fun getFavorites(context: Context): List<Product> {
        val sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(FAVORITES_KEY, null)

        return if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun isFavorite(context: Context, productId: Int): Boolean {
        return getFavorites(context).any { it.id == productId }
    }

    fun toggleFavorite(context: Context, product: Product) {
        if (isFavorite(context, product.id)) {
            removeFromFavorites(context, product.id)
        } else {
            addToFavorites(context, product)
        }
    }

    private fun saveFavorites(context: Context, favorites: List<Product>) {
        val sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE)
        val json = gson.toJson(favorites)
        sharedPreferences.edit().putString(FAVORITES_KEY, json).apply()
    }
}