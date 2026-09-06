package com.manoj.productsmvvm.domain.model

/** A UI-independent model. Network DTOs and Room entities never escape the data layer. */
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val thumbnailUrl: String,
    val isFavorite: Boolean,
)
