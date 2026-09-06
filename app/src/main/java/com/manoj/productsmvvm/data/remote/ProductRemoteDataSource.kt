package com.manoj.productsmvvm.data.remote

import javax.inject.Inject

interface ProductRemoteDataSource {
    suspend fun getProducts(): List<ProductDto>
}

class RetrofitProductRemoteDataSource @Inject constructor(
    private val api: ProductApi,
) : ProductRemoteDataSource {
    // Retrofit suspend functions are main-safe: they do not block the calling thread.
    override suspend fun getProducts(): List<ProductDto> = api.getProducts().products
}
