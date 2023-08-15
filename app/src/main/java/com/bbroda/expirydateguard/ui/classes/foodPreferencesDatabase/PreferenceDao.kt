package com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PreferenceDao {
    @Query("SELECT * FROM preference")
    suspend fun getAll(): List<Preference>

    /*@Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>*/

    @Query("SELECT * FROM preference WHERE uid = (:preferenceID)")
    suspend fun loadByID(preferenceID: Int): Preference

    @Insert
    suspend fun insertAll(vararg preferences: Preference)

    @Update
    suspend fun updatePreference(preference: Preference)

    @Delete
    suspend fun delete(recipes: Preference)
}