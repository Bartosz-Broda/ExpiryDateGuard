package com.bbroda.expirydateguard.ui.classes.groceryRetrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface OpenFoodFactsAPI {
    @GET("search")
    suspend fun callForProduct(@QueryMap parameters: Map<String,String>): Response<CallResult>
}