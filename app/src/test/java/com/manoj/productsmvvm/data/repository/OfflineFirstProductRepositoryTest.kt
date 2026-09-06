package com.manoj.productsmvvm.data.repository

import app.cash.turbine.test
import com.manoj.productsmvvm.data.local.ProductEntity
import com.manoj.productsmvvm.data.local.ProductLocalDataSource
import com.manoj.productsmvvm.data.remote.ProductDto
import com.manoj.productsmvvm.data.remote.ProductRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineFirstProductRepositoryTest {
    private val dto = ProductDto(1, "Phone", "A phone", "mobile", 499.0, 4.5, "image")

    @Test
    fun `refresh writes remote data and observers receive mapped domain data`() = runTest {
        val local = FakeLocalDataSource()
        val repository = OfflineFirstProductRepository(FakeRemoteDataSource(listOf(dto)), local)

        repository.observeProducts().test {
            assertEquals(emptyList<Any>(), awaitItem())
            repository.refresh()
            val product = awaitItem().single()
            assertEquals("Phone", product.title)
            assertEquals(false, product.isFavorite)
        }
    }

    @Test
    fun `favorite changes come back through the room-shaped stream`() = runTest {
        val local = FakeLocalDataSource()
        val repository = OfflineFirstProductRepository(FakeRemoteDataSource(listOf(dto)), local)
        repository.refresh()

        repository.observeProduct(1).test {
            assertEquals(false, awaitItem()?.isFavorite)
            repository.toggleFavorite(1)
            assertEquals(true, awaitItem()?.isFavorite)
        }
    }
}

private class FakeRemoteDataSource(private val products: List<ProductDto>) : ProductRemoteDataSource {
    override suspend fun getProducts() = products
}

private class FakeLocalDataSource : ProductLocalDataSource {
    private val products = MutableStateFlow<List<ProductEntity>>(emptyList())

    override fun observeProducts(): Flow<List<ProductEntity>> = products
    override fun observeProduct(id: Int): Flow<ProductEntity?> =
        products.map { list -> list.find { it.id == id } }

    override suspend fun replaceFromNetwork(products: List<ProductDto>) {
        this.products.value = products.map {
            ProductEntity(it.id, it.title, it.description, it.category, it.price, it.rating, it.thumbnailUrl, false)
        }
    }

    override suspend fun toggleFavorite(id: Int) {
        products.value = products.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
