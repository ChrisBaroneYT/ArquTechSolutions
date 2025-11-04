package com.example.myapplicationarturocashfaster

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: Int,
    val category: ProductCategory,
    val isForRent: Boolean = false,
    val rentPricePerDay: Double? = null,
    val stock: Int = 0,
    val unit: String = "unidad"
) : Parcelable

enum class ProductCategory {
    HERRAMIENTAS, MATERIALES, EQUIPOS, SEGURIDAD, ALQUILER, ELECTRICIDAD, FONTANERIA
}