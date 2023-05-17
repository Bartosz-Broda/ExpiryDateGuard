package com.bbroda.expirydateguard.ui.classes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>

    /*@Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>*/

    @Insert
    suspend fun insertAll(vararg products: Product)

    @Delete
    suspend fun delete(products: Product)
}