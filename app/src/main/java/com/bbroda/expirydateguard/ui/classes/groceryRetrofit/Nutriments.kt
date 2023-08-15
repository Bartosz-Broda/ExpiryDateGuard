package com.bbroda.expirydateguard.ui.classes.groceryRetrofit

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Nutriments(

    @JsonAnySetter
    @get:JsonAnyGetter
    val allNutriments: Map<String, Any> = hashMapOf()

)
