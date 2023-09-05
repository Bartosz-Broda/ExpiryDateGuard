package com.bbroda.expirydateguard.ui.classes.FoodTypesDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoodTypesDao {
    @Query("SELECT * FROM type")
    suspend fun getAll(): List<Type>

    /*@Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>*/

    @Query("SELECT * FROM type WHERE uid = (:typeID)")
    suspend fun loadByID(typeID: Int): Type

    @Insert
    suspend fun insertAll(types: List<Type>)

    @Update
    suspend fun updatePreference(type: Type)

    @Delete
    suspend fun delete(types: Type)

    @Query("DELETE FROM Type")
    suspend fun nukeTable()
}