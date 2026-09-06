package com.manoj.productsmvvm.data.repository

import com.manoj.productsmvvm.data.local.ProductEntity
import com.manoj.productsmvvm.data.local.ProductLocalDataSource
import com.manoj.productsmvvm.data.remote.ProductRemoteDataSource
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstProductRepository @Inject constructor(
    private val remote: ProductRemoteDataSource,
    private val local: ProductLocalDataSource,
) : ProductRepository {
    override fun observeProducts(): Flow<List<Product>> =
        local.observeProducts().map { products -> products.map(ProductEntity::toDomain) }

    override fun observeProduct(id: Int): Flow<Product?> =
        local.observeProduct(id).map { it?.toDomain() }

    override suspend fun refresh() {
        // Network results are persisted first; the UI only observes Room (single source of truth).
        local.replaceFromNetwork(remote.getProducts())
    }

    override suspend fun toggleFavorite(id: Int) = local.toggleFavorite(id)
}

private fun ProductEntity.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    price = price,
    rating = rating,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
)
