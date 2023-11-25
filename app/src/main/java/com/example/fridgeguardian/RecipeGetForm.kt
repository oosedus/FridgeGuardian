package com.example.fridgeguardian

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeGetForm {
    @GET("/api/{keyId}/{serviceId}/{dataType}/{startIdx}/{endIdx}/RCP_NM={recipeName}")
    fun getRecipes(
        @Path("keyId") keyId: String,
        @Path("serviceId") serviceId: String,
        @Path("dataType") dataType: String,
        @Path("startIdx") startIdx: Int,
        @Path("endIdx") endIdx: Int,
        @Path("recipeName") recipeName: String
    ): Call<RecipeResponse>
}