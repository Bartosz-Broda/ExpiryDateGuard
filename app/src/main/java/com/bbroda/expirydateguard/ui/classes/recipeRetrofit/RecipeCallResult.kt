package com.bbroda.expirydateguard.ui.classes.recipeRetrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
@JsonIgnoreProperties(ignoreUnknown = true)
data class RecipeCallResult(
    @JsonProperty("hits") val hits: MutableList<Recipes>?,
    @JsonProperty("count") val count: Int?,

)
