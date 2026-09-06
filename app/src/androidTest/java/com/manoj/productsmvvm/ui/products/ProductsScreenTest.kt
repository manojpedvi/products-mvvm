package com.manoj.productsmvvm.ui.products

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.manoj.productsmvvm.domain.model.Product
import com.manoj.productsmvvm.ui.theme.ProductsMvvmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ProductsScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun productStateRendersItsContent() {
        composeRule.setContent {
            ProductsMvvmTheme {
                ProductsScreen(
                    state = ProductsUiState(
                        products = listOf(
                            Product(1, "Interview Laptop", "", "computers", 999.0, 4.8, "", false),
                        ),
                        isRefreshing = false,
                    ),
                    snackbarHostState = SnackbarHostState(),
                    onQueryChanged = {},
                    onRefresh = {},
                    onProductClick = {},
                    onFavoriteClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Interview Laptop").assertIsDisplayed()
        composeRule.onNodeWithText("computers").assertIsDisplayed()
    }

    @Test
    fun clickingProductReturnsItsIdForNavigation() {
        var clickedId: Int? = null
        composeRule.setContent {
            ProductsMvvmTheme {
                ProductsScreen(
                    state = ProductsUiState(
                        products = listOf(
                            Product(42, "Clickable Phone", "", "mobile", 499.0, 4.4, "", false),
                        ),
                        isRefreshing = false,
                    ),
                    snackbarHostState = SnackbarHostState(),
                    onQueryChanged = {},
                    onRefresh = {},
                    onProductClick = { clickedId = it },
                    onFavoriteClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Clickable Phone").performClick()
        composeRule.runOnIdle { assertEquals(42, clickedId) }
    }
}
