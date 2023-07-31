package com.bbroda.expirydateguard.ui.classes.retrofit

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

data class Nutriments(

    @JsonAnySetter
    @get:JsonAnyGetter
    val allNutriments: Map<String, Any> = hashMapOf()

)
