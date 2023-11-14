package com.example.fridgeguardian

import com.google.gson.annotations.SerializedName

data class CookRcp01(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("row") val rows: List<RecipeDataForm>
)
