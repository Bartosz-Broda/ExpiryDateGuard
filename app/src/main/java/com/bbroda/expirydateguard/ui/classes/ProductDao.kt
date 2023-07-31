package com.bbroda.expirydateguard.ui.classes

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>

    /*@Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>*/

    @Query("SELECT * FROM product WHERE uid = (:productID)")
    suspend fun loadByID(productID: Int): Product

    @Insert
    suspend fun insertAll(vararg products: Product)

    @Update
    suspend fun updateProduct(products: Product)

    @Delete
    suspend fun delete(products: Product)
}