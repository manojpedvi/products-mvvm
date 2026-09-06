package com.manoj.productsmvvm.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.manoj.productsmvvm.R
import com.manoj.productsmvvm.domain.model.Product
import java.text.NumberFormat

@Composable
fun ProductDetailsRouteScreen(onBack: () -> Unit, viewModel: ProductDetailsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProductDetailsScreen(state, onBack, viewModel::toggleFavorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    state: ProductDetailsUiState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.product == null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.product_unavailable))
                    Button(onClick = onBack) { Text(stringResource(R.string.go_back)) }
                }
                else -> ProductDetails(state.product, onFavoriteClick)
            }
        }
    }
}

@Composable
private fun ProductDetails(product: Product, onFavoriteClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = product.thumbnailUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(280.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                product.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onFavoriteClick) {
                Text(stringResource(if (product.isFavorite) R.string.saved else R.string.save))
            }
        }
        Text(product.category, style = MaterialTheme.typography.labelLarge)
        Text(
            NumberFormat.getCurrencyInstance().format(product.price),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(stringResource(R.string.rating, product.rating))
        Spacer(Modifier.height(4.dp))
        Text(product.description, style = MaterialTheme.typography.bodyLarge)
    }
}
