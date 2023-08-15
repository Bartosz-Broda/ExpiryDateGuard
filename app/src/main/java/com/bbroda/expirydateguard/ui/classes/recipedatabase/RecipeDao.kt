package com.bbroda.expirydateguard.ui.classes.recipedatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    suspend fun getAll(): List<Recipe>

    /*@Query("SELECT * FROM product WHERE uid IN (:productIds)")
    fun loadAllByIds(productIds: IntArray): List<Product>*/

    @Query("SELECT * FROM recipe WHERE uid = (:recipeID)")
    suspend fun loadByID(recipeID: Int): Recipe

    @Insert
    suspend fun insertAll(vararg recipes: Recipe)

    @Update
    suspend fun updateRecipe(recipes: Recipe)

    @Delete
    suspend fun delete(recipes: Recipe)

    @Query("DELETE FROM Recipe")
    suspend fun nukeTable()
}