package com.manoj.productsmvvm.ui.products

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.manoj.productsmvvm.MainDispatcherRule
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.domain.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query filters products and is saved for recreation`() = runTest(mainDispatcherRule.dispatcher) {
        val savedState = SavedStateHandle()
        val repository = FakeProductRepository()
        val viewModel = ProductsViewModel(repository, savedState)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onQueryChanged("lap")
        advanceUntilIdle()

        assertEquals(listOf("Laptop"), viewModel.uiState.value.products.map(Product::title))
        assertEquals("lap", savedState.get<String>("product_query"))
    }

    @Test
    fun `failed refresh emits transient effect and keeps cached data`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeProductRepository()
        val viewModel = ProductsViewModel(repository, SavedStateHandle())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        advanceUntilIdle()
        repository.failRefresh = true

        viewModel.effects.test {
            viewModel.refresh()
            advanceUntilIdle()
            assertEquals(ProductsEffect.RefreshFailed, awaitItem())
            assertEquals(2, viewModel.uiState.first().products.size)
        }
    }

    @Test
    fun `empty cache and failed initial refresh ends in retryable empty state`() =
        runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeProductRepository(initialProducts = emptyList(), failRefresh = true)
        val viewModel = ProductsViewModel(repository, SavedStateHandle())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isInitialLoading)
        assertEquals(true, viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `query matches category without regard to case`() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = ProductsViewModel(FakeProductRepository(), SavedStateHandle())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onQueryChanged("MOBILE")
        advanceUntilIdle()

        assertEquals(listOf("Phone"), viewModel.uiState.value.products.map(Product::title))
    }
}

private class FakeProductRepository(
    initialProducts: List<Product> = listOf(
            Product(1, "Laptop", "Portable", "computers", 999.0, 4.8, "", false),
            Product(2, "Phone", "Pocket-sized", "mobile", 499.0, 4.4, "", false),
    ),
    var failRefresh: Boolean = false,
) : ProductRepository {
    private val products = MutableStateFlow(initialProducts)

    override fun observeProducts(): Flow<List<Product>> = products
    override fun observeProduct(id: Int): Flow<Product?> =
        products.map { list -> list.find { it.id == id } }
    override suspend fun refresh() {
        if (failRefresh) error("Network unavailable")
    }
    override suspend fun toggleFavorite(id: Int) = Unit
}
