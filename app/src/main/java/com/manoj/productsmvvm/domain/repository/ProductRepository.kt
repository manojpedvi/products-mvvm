package com.manoj.productsmvvm.domain.repository

import com.manoj.productsmvvm.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    /** Room is the source of truth, so these streams continue to work offline. */
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(id: Int): Flow<Product?>
    suspend fun refresh()
    suspend fun toggleFavorite(id: Int)
}
