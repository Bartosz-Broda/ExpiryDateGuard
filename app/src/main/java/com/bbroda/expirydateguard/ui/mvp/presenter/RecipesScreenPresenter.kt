package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.RecipesScreenActivity
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.PreferenceDatabase
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import com.bbroda.expirydateguard.ui.classes.recipedatabase.RecipeDatabase
import com.bbroda.expirydateguard.ui.mvp.model.RecipesScreenModel
import com.bbroda.expirydateguard.ui.mvp.view.RecipesScreenView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import kotlin.properties.Delegates

class RecipesScreenPresenter(val view: RecipesScreenView, val model: RecipesScreenModel, val activity: RecipesScreenActivity){

    var ingredients by Delegates.notNull<String>()
    private var ingredientsQuantity by Delegates.notNull<Int>()
    val db = RecipeDatabase.getDatabase(activity)
    val preferencesDB = PreferenceDatabase.getDatabase(activity)
    val productDB = ProductsDatabase.getDatabase(activity)
    init{
        val extras = activity.intent.extras
        if (extras != null) {
            ingredients = extras.getString("ingredients").toString()
            ingredientsQuantity = extras.getInt("ingredients_quantity")
            activity.lifecycleScope.launch {model.makeRecipeApiCall(ingredients,preferencesDB,productDB)}
        }else{
            view.showMessage("${activity.getString(R.string.main_menu_api_failed)} \n\nError: No Ingredients passed")
        }

    }



    @Subscribe
    fun onRecipeApiCallSuccess(event: RecipesScreenModel.ApiCallSuccessful){
        view.hideProgressBar()
        try{
            if (event.result.body()!!.hits!!.isEmpty()){
                view.hideProgressBar()
                view.showNoRecipiesTextView()
            }
            else{
            event.result.body()!!.hits?.let { view.initRecyclerView(it, event.products ) }
            }
        }catch (e:Exception){
            view.showMessage("${activity.getString(R.string.main_menu_api_failed)} \n\nError: Api call successfull, but $e")
            Log.d(TAG, "onRecipeApiCallSuccess: Exception: $e")
        }
    }

    @Subscribe
    fun onRecipeApiCallFail(event: RecipesScreenModel.ApiCallFailed){
        view.hideProgressBar()
        if(event.exception.contains("UnknownHostException")){
            view.showMessage(activity.getString(R.string.check_internet_connection))
        }else {
            view.showMessage("${activity.getString(R.string.main_menu_api_failed)} \n\nError code: ${event.exception}")
        }
    }

    @Subscribe
    fun onAddToFavourite(event:RecipesScreenView.AddToFavourite){
        activity.lifecycleScope.launch {model.addRecipeToFavourites(db,event.recipe)}
    }

    @Subscribe
    fun recipeAddedToFavouritesSuccessFully(event: RecipesScreenModel.RecipeAddedToFavourites){
        view.showToast(activity.getString(R.string.recipeAdded))
    }

    @Subscribe
    fun recipeAddedToFavouritesSuccessFully(event: RecipesScreenModel.RecipeNotAddedToFavourites){
        view.showToast(activity.getString(R.string.recipeNotAdded))
    }

    @Subscribe
    fun recipeAddedToFavouritesSuccessFully(event: RecipesScreenModel.RecipeAlreadyAdded){
        view.showToast(activity.getString(R.string.recipe_alredy_added))
    }



}
