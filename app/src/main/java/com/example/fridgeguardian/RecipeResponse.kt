package com.example.fridgeguardian

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    @SerializedName("COOKRCP01") val cookRcp01: CookRcp01
)