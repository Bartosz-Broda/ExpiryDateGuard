package com.bbroda.expirydateguard.ui.classes.recipeRetrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class Recipes(
    @JsonProperty("recipe") val recipe: RecipeDetails?,
):Serializable
