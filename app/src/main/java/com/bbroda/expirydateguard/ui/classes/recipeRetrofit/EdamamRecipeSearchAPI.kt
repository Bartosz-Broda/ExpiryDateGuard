package com.bbroda.expirydateguard.ui.classes.recipeRetrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface EdamamRecipeSearchAPI {
    @GET("v2")
    suspend fun callForRecipe(
        @QueryMap parameters: Map<String, String>,
        @Query("health") healthPreferences: List<String>?
    ): Response<RecipeCallResult>
}