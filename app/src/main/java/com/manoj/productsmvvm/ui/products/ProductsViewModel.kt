package com.manoj.productsmvvm.ui.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val query: String = "",
    val isRefreshing: Boolean = true,
) {
    val isInitialLoading: Boolean get() = isRefreshing && products.isEmpty()
    val isEmpty: Boolean get() = !isRefreshing && products.isEmpty()
}

sealed interface ProductsEffect {
    data object RefreshFailed : ProductsEffect
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val query = savedStateHandle.getStateFlow(QUERY_KEY, "")
    private val isRefreshing = MutableStateFlow(true)

    private val _effects = MutableSharedFlow<ProductsEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProductsEffect> = _effects.asSharedFlow()
    private var refreshJob: Job? = null

    // StateFlow always has a latest value, making it the correct fit for renderable UI state.
    val uiState: StateFlow<ProductsUiState> = combine(
        repository.observeProducts(),
        query,
        isRefreshing,
    ) { products, search, refreshing ->
        val filtered = products.filter {
            it.title.contains(search, ignoreCase = true) ||
                it.category.contains(search, ignoreCase = true)
        }
        ProductsUiState(filtered, search, refreshing)
    }.stateIn(
        scope = viewModelScope,
        // Stop upstream work shortly after the UI disappears, while surviving quick rotations.
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ProductsUiState(),
    )

    init {
        refresh()
    }

    fun onQueryChanged(value: String) {
        // SavedStateHandle restores this small piece of UI state after process recreation.
        savedStateHandle[QUERY_KEY] = value
    }

    fun refresh() {
        // Coalesce repeated swipe/retry actions so only one network refresh runs at a time.
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            isRefreshing.value = true
            runCatching { repository.refresh() }
                .onFailure { _effects.emit(ProductsEffect.RefreshFailed) }
            isRefreshing.value = false
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { repository.toggleFavorite(id) }
    }

    private companion object {
        const val QUERY_KEY = "product_query"
    }
}
