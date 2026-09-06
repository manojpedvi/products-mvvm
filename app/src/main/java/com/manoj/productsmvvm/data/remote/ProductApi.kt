package com.manoj.productsmvvm.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface ProductApi {
    @GET("products?limit=100")
    suspend fun getProducts(): ProductResponse
}

@Serializable
data class ProductResponse(val products: List<ProductDto>)

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val rating: Double = 0.0,
    @SerialName("thumbnail") val thumbnailUrl: String,
)
