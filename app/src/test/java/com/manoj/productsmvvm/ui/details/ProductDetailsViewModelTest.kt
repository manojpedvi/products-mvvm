package com.manoj.productsmvvm.ui.details

import androidx.lifecycle.SavedStateHandle
import com.manoj.productsmvvm.MainDispatcherRule
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.domain.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `route id selects product and favorite action updates observed state`() =
        runTest(mainDispatcherRule.dispatcher) {
        val repository = DetailFakeRepository()
        // Navigation stores typed route fields in SavedStateHandle for the destination ViewModel.
        val viewModel = ProductDetailsViewModel(SavedStateHandle(mapOf("productId" to 2)), repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals("Phone", viewModel.uiState.value.product?.title)
        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.value.product?.isFavorite)
    }
}

private class DetailFakeRepository : ProductRepository {
    private val products = MutableStateFlow(
        listOf(
            Product(1, "Laptop", "", "computers", 999.0, 4.8, "", false),
            Product(2, "Phone", "", "mobile", 499.0, 4.4, "", false),
        ),
    )

    override fun observeProducts(): Flow<List<Product>> = products
    override fun observeProduct(id: Int): Flow<Product?> = products.map { list -> list.find { it.id == id } }
    override suspend fun refresh() = Unit
    override suspend fun toggleFavorite(id: Int) {
        products.value = products.value.map { product ->
            if (product.id == id) product.copy(isFavorite = !product.isFavorite) else product
        }
    }
}
