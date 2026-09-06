package com.manoj.productsmvvm.data.local

import androidx.room.withTransaction
import com.manoj.productsmvvm.data.remote.ProductDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ProductLocalDataSource {
    fun observeProducts(): Flow<List<ProductEntity>>
    fun observeProduct(id: Int): Flow<ProductEntity?>
    suspend fun replaceFromNetwork(products: List<ProductDto>)
    suspend fun toggleFavorite(id: Int)
}

class RoomProductLocalDataSource @Inject constructor(
    private val database: ProductsDatabase,
    private val dao: ProductDao,
) : ProductLocalDataSource {
    override fun observeProducts() = dao.observeAll()
    override fun observeProduct(id: Int) = dao.observeById(id)

    override suspend fun replaceFromNetwork(products: List<ProductDto>) {
        // The transaction prevents observers from seeing an empty/partially refreshed cache.
        database.withTransaction {
            val favorites = dao.favoriteIds().toSet()
            dao.deleteAll()
            dao.insertAll(products.map { it.toEntity(it.id in favorites) })
        }
    }

    override suspend fun toggleFavorite(id: Int) = dao.toggleFavorite(id)
}

private fun ProductDto.toEntity(isFavorite: Boolean) = ProductEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    rating = rating,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
)
