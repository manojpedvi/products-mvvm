package com.manoj.productsmvvm.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailsUiState(
    val product: Product? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository,
) : ViewModel() {
    // The key matches ProductDetailsRoute.productId. Reading the primitive directly also keeps
    // this ViewModel testable on the JVM, where Android's SavedState decoder is not available.
    private val productId = checkNotNull(savedStateHandle.get<Int>(PRODUCT_ID_KEY))

    val uiState: StateFlow<ProductDetailsUiState> = repository.observeProduct(productId)
        .map { ProductDetailsUiState(product = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductDetailsUiState())

    fun toggleFavorite() {
        viewModelScope.launch { repository.toggleFavorite(productId) }
    }

    private companion object {
        const val PRODUCT_ID_KEY = "productId"
    }
}
