package com.bbroda.expirydateguard.ui.mvp.presenter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.RecipesScreenActivity
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.PreferenceDatabase
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
    init{
        val extras = activity.intent.extras
        if (extras != null) {
            ingredients = extras.getString("ingredients").toString()
            ingredientsQuantity = extras.getInt("ingredients_quantity")
            activity.lifecycleScope.launch {model.makeRecipeApiCall(ingredients,preferencesDB)}
        }else{
            view.showMessage(activity.getString(R.string.main_menu_api_failed))
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
            event.result.body()!!.hits?.let { view.initRecyclerView(it, ingredientsQuantity ) }
            }
        }catch (e:Exception){
            view.showMessage(activity.getString(R.string.main_menu_api_failed))
            Log.d(TAG, "onRecipeApiCallSuccess: Exception: $e")
        }
    }

    @Subscribe
    fun onRecipeApiCallFail(event: RecipesScreenModel.ApiCallFailed){
        view.hideProgressBar()
        view.showMessage(activity.getString(R.string.main_menu_api_failed))
    }
}
