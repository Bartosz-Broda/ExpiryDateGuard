package com.bbroda.expirydateguard.ui.classes.recipeRetrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object RecipeRetrofitHelper {
    fun getInstance(): Retrofit {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        val baseURL = "https://api.edamam.com/api/recipes/"
        return Retrofit.Builder().baseUrl(baseURL).client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create())
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }
}