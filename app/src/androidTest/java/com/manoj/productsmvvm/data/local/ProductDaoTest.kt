package com.manoj.productsmvvm.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.manoj.productsmvvm.data.remote.ProductDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: ProductsDatabase
    private lateinit var dao: ProductDao
    private lateinit var localDataSource: ProductLocalDataSource

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ProductsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.productDao()
        localDataSource = RoomProductLocalDataSource(database, dao)
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun togglingFavoriteUpdatesObservedEntity() = runTest {
        dao.insertAll(listOf(ProductEntity(1, "Phone", "", "mobile", 10.0, 4.0, "", false)))
        dao.toggleFavorite(1)
        assertTrue(dao.observeById(1).first()!!.isFavorite)
    }

    @Test
    fun networkReplacementPreservesLocalFavorite() = runTest {
        val old = ProductDto(1, "Old title", "", "mobile", 10.0, 4.0, "old")
        localDataSource.replaceFromNetwork(listOf(old))
        localDataSource.toggleFavorite(1)

        val refreshed = old.copy(title = "Updated title", price = 12.0)
        localDataSource.replaceFromNetwork(listOf(refreshed))

        val stored = localDataSource.observeProduct(1).first()!!
        assertTrue(stored.isFavorite)
        org.junit.Assert.assertEquals("Updated title", stored.title)
    }
}
