package com.bbroda.expirydateguard.ui.mvp.presenter

import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.ui.activities.FavouriteRecipesActivity
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.recipedatabase.RecipeDatabase
import com.bbroda.expirydateguard.ui.mvp.model.FavouriteRecipesModel
import com.bbroda.expirydateguard.ui.mvp.view.FavouriteRecipesView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

class FavouriteRecipesPresenter(val view: FavouriteRecipesView, val model: FavouriteRecipesModel, val activity: FavouriteRecipesActivity) {

    val recipeDB = RecipeDatabase.getDatabase(activity)
    val productsDB = ProductsDatabase.getDatabase(activity)

    init {
        activity.lifecycleScope.launch{model.getRecipesAndProductsFromDatabase(recipeDB,productsDB)}
    }
    @Subscribe
    fun onrecipeDeleted(event: FavouriteRecipesModel.RecipeDeleted) {
        view.refreshRecipies(event.position)
    }

    @Subscribe
    fun onRemoveFromFavouriteClicked(event: FavouriteRecipesView.RemoveFromFavourite){
        activity.lifecycleScope.launch { model.removeRecipeFromFavourites(recipeDB,event.recipe, event.position) }
    }

    @Subscribe
    fun recipesFetchedFromDatabase(event: FavouriteRecipesModel.RecipesFetchedFromDatabase){
        if(!event.recipes.isNullOrEmpty()){
            view.initRecyclerView(event.recipes.toMutableList(), event.products)
        }else{
            view.showNoRecipesInfo()
        }
    }
}
