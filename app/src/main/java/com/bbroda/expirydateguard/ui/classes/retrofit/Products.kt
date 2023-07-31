package com.bbroda.expirydateguard.ui.classes.retrofit

import com.fasterxml.jackson.annotation.JsonProperty

data class Products(
    @JsonProperty("code") val code: String,
    @JsonProperty ("product_name") val product_name: String,
    @JsonProperty ("category_properties") val category_properties: CategoryProperties?,
    @JsonProperty ("ingredients_text") val ingredients_text: String?,
    @JsonProperty ("nutriments") val nutriments: Nutriments?,
    @JsonProperty ("image_front_small_url") val imageUrl: String?
)
