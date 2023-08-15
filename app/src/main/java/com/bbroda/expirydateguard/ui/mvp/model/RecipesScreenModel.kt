package com.bbroda.expirydateguard.ui.mvp.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.Preference
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.PreferenceDatabase
import com.bbroda.expirydateguard.ui.classes.recipeRetrofit.EdamamRecipeSearchAPI
import com.bbroda.expirydateguard.ui.classes.recipeRetrofit.RecipeCallResult
import com.bbroda.expirydateguard.ui.classes.recipeRetrofit.RecipeRetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.Response


class RecipesScreenModel (var bus: EventBus, val activityContext: Context){

    @SuppressLint("SuspiciousIndentation")
    suspend fun makeRecipeApiCall(ingredients: String ,database: PreferenceDatabase){
        Log.d(ContentValues.TAG, "makeApiCall: Calling for recipe with products: $ingredients")
        val edamamRecipeSearchAPI = RecipeRetrofitHelper.getInstance().create(EdamamRecipeSearchAPI::class.java)

        try {
            Log.d(ContentValues.TAG, "makeApiCall: TRYING TO FETCH RECIPES")

            val apiCallMapParameters = mutableMapOf(
                "type" to "public",
                "q" to ingredients,
                "app_id" to "4c4124f3",
                "app_key" to "2b5df08a3ad4610d5404bde688ea9100"
            )

            val healthParameters = retrieveFoodPreferences(database)
            val healthPrefStingList = mutableListOf<String>()

            if(healthParameters.isNotEmpty()) {
                Log.d(TAG, "makeRecipeApiCall: HEALTHPARAMETERS: $healthParameters")
                for (i in healthParameters) {
                    if(i.isChecked) {
                        healthPrefStingList.add(i.apiLabel)
                        Log.d(TAG, "makeRecipeApiCall: putting parameter ${i.apiLabel} into apicallparameters")
                        Log.d(TAG, "makeRecipeApiCall: apiCallParameters: $apiCallMapParameters")
                    }
                }

            }else{
                Log.d(TAG, "makeRecipeApiCall: healthparameters is empty!")
            }

            Log.d(TAG, "makeRecipeApiCall: APICALLMAPPARAMETERS: $apiCallMapParameters")
            Log.d(TAG, "makeRecipeApiCall: ")
            val result = edamamRecipeSearchAPI.callForRecipe(apiCallMapParameters,healthPrefStingList)
            Log.d(TAG, "makeRecipeApiCall: URL: ${result.raw().request.url}")

            Log.d(ContentValues.TAG, "makeRecipeApiCall: SUCCESS! RESULT: ${result.body()}")

            withContext(Dispatchers.Main) {
                bus.post(ApiCallSuccessful(result))
            }

        } catch (e: java.lang.Exception) {
            Log.d(ContentValues.TAG, "RECIPE API CALL FAILED: $e")

            withContext(Dispatchers.Main){
                bus.post(ApiCallFailed())
            }
        }

    }

        suspend fun retrieveFoodPreferences(database: PreferenceDatabase): List<Preference>{
            val list = database.preferenceDao().getAll()
            return list
        }



    class ApiCallSuccessful(val result: Response<RecipeCallResult>)
    class ApiCallFailed
}
