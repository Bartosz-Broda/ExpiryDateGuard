package com.bbroda.expirydateguard.ui.classes.groceryRetrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CategoryProperties(
    @JsonProperty("ciqual_food_name:en") val ciqual_food_name :String?,
    @JsonProperty("ciqual_food_name:fr") val ciqual_food_name_fr :String?
)
