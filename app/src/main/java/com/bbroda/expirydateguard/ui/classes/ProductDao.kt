package com.bbroda.expirydateguard.ui.classes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>

    @Insert
    fun insertAll(vararg products: Product)

    @Delete
    fun delete(products: Product)
}