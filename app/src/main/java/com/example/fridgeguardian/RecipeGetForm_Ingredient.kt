package com.example.fridgeguardian

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeGetForm_Ingredient {
    @GET("/api/{keyId}/{serviceId}/{dataType}/{startIdx}/{endIdx}/RCP_PARTS_DTLS={IngredientName}")
    fun getRecipes(
        @Path("keyId") keyId: String,
        @Path("serviceId") serviceId: String,
        @Path("dataType") dataType: String,
        @Path("startIdx") startIdx: Int,
        @Path("endIdx") endIdx: Int,
        @Path("IngredientName") IngredientName: String
    ): Call<RecipeResponse>
}