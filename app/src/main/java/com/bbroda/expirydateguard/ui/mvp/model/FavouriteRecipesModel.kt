package com.bbroda.expirydateguard.ui.mvp.model

import android.content.ContentValues
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.recipedatabase.Recipe
import com.bbroda.expirydateguard.ui.classes.recipedatabase.RecipeDatabase
import org.greenrobot.eventbus.EventBus

class FavouriteRecipesModel(var bus:EventBus) {


    suspend fun removeRecipeFromFavourites(database: RecipeDatabase, recipe: Recipe){

        val databaseRecipes = database.recipeDao().getAll()

        database.recipeDao().delete(recipe)
        Log.d(ContentValues.TAG, "removeRecipeFromFavourites: RECIPE REMOVED FROM DATABASE. DATABASE CONTENT: ${database.recipeDao().getAll()}")
    }

    suspend fun getRecipesAndProductsFromDatabase(database: RecipeDatabase, productsDatabase: ProductsDatabase){
        val recipes = database.recipeDao().getAll()
        val products = productsDatabase.productDao().getAll()
        bus.post(RecipesFetchedFromDatabase(recipes, products))
    }


    class RecipeDeleted()
    class RecipesFetchedFromDatabase(val recipes: List<Recipe>, val products: List<Product>)
}
