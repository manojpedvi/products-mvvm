package com.manoj.productsmvvm.ui.navigation

import kotlinx.serialization.Serializable

// Typed routes make destination arguments compile-time checked instead of stringly typed.
@Serializable
data object ProductsRoute

@Serializable
data class ProductDetailsRoute(val productId: Int)
