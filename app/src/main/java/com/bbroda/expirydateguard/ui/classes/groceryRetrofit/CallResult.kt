package com.bbroda.expirydateguard.ui.classes.groceryRetrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

//data class CallResult
//According to JSON response
@JsonIgnoreProperties(ignoreUnknown = true)
data class CallResult(
    @JsonProperty ("count") val count: Int?,
    @JsonProperty ("page") val page: Int?,
    @JsonProperty ("page_count") val page_count: Int?,
    @JsonProperty ("page_size") val page_size: Int?,
    @JsonProperty ("products") val products: Array<Products>,
    @JsonProperty ("skip") val skip: Int?,
)
