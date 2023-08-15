package com.bbroda.expirydateguard.ui.classes.recipeRetrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecipeDetails(
    @JsonProperty("label") val label: String?,
    @JsonProperty("image") val image: String?,
    @JsonProperty("source") val source: String?,
    @JsonProperty("url") val url: String?,
    @JsonProperty("dietLabels") val dietLabels: List<String>?,
    @JsonProperty("healthLabels") val healthLabels: List<String>?,
    @JsonProperty("cautions") val cautions: List<String>?,
    @JsonProperty("ingredientLines") val ingredientLines: List<String>?,
    @JsonProperty("calories") val calories: Float?,
    @JsonProperty("mealType") val mealType: List<String>?,
    @JsonProperty("totalTime") val time: String?
    ):Serializable
