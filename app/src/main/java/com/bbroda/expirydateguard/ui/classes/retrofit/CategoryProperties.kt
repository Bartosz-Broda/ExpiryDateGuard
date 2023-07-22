package com.bbroda.expirydateguard.ui.classes.retrofit

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryProperties(
    @JsonProperty("ciqual_food_name:en") val ciqual_food_name :String?,
    @JsonProperty("ciqual_food_name:fr") val ciqual_food_name_fr :String?
)
