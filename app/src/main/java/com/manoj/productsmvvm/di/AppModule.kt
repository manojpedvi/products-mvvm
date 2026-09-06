package com.manoj.productsmvvm.di

import android.content.Context
import androidx.room.Room
import com.manoj.productsmvvm.data.local.ProductDao
import com.manoj.productsmvvm.data.local.ProductLocalDataSource
import com.manoj.productsmvvm.data.local.ProductsDatabase
import com.manoj.productsmvvm.data.local.RoomProductLocalDataSource
import com.manoj.productsmvvm.data.remote.ProductApi
import com.manoj.productsmvvm.data.remote.ProductRemoteDataSource
import com.manoj.productsmvvm.data.remote.RetrofitProductRemoteDataSource
import com.manoj.productsmvvm.data.repository.OfflineFirstProductRepository
import com.manoj.productsmvvm.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingsModule {
    @Binds abstract fun bindRepository(impl: OfflineFirstProductRepository): ProductRepository
    @Binds abstract fun bindRemote(impl: RetrofitProductRemoteDataSource): ProductRemoteDataSource
    @Binds abstract fun bindLocal(impl: RoomProductLocalDataSource): ProductLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProductsDatabase =
        Room.databaseBuilder(context, ProductsDatabase::class.java, "products.db").build()

    @Provides
    fun provideProductDao(database: ProductsDatabase): ProductDao = database.productDao()

    @Provides
    @Singleton
    fun provideProductApi(): ProductApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProductApi::class.java)
    }
}
