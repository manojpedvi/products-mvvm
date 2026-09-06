package com.manoj.productsmvvm.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.manoj.productsmvvm.ui.details.ProductDetailsRouteScreen
import com.manoj.productsmvvm.ui.navigation.ProductDetailsRoute
import com.manoj.productsmvvm.ui.navigation.ProductsRoute
import com.manoj.productsmvvm.ui.products.ProductsRouteScreen

@Composable
fun ProductsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ProductsRoute) {
        composable<ProductsRoute> {
            ProductsRouteScreen(
                onProductClick = { id -> navController.navigate(ProductDetailsRoute(id)) },
                viewModel = hiltViewModel(),
            )
        }
        composable<ProductDetailsRoute> {
            ProductDetailsRouteScreen(
                onBack = navController::navigateUp,
                viewModel = hiltViewModel(),
            )
        }
    }
}
