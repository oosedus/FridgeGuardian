package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import Account.MyPageActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeSearchWithIngredientActivityDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_search_ingredient_activity2)

        val selectedIngredients = intent.getStringArrayListExtra("selectedIngredients")
        Log.d("ITM", selectedIngredients.toString())

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Recipe"
        setSupportActionBar(toolbar)

        val bottomNav = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.selectedItemId = R.id.nav_recipe
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_recipe -> {
                    true
                }
                R.id.nav_community -> {
                    val intent = Intent(this, CommunityActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_mypage -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        if (selectedIngredients != null) {
            RecipeSearchView(selectedIngredients)
        }
    }

    private fun RecipeSearchView(recipes : ArrayList<String>){
        // init SearchView

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit= Retrofit.Builder()
            .baseUrl("http://openapi.foodsafetykorea.go.kr")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val RecipeServiceGo = retrofit.create(RecipeGetForm_Ingredient::class.java)
        val recipeCountMap: MutableMap<RecipeDataForm, Int> = mutableMapOf()
        // retrofit 사용하면 비동기 때문에 오류 뜸
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            for (ingredient in recipes) {
                val call = RecipeServiceGo.getRecipes(
                    "57777c04fcc34d9a978b",
                    "COOKRCP01",
                    "json",
                    1,
                    1000,
                    "\"${ingredient}\""
                )
                try {
                    val response = call.execute()
                    if (response.isSuccessful) {
                        val recipeData = response.body()
                        if (recipeData != null) {
                            for (recipe in recipeData.cookRcp01.rows) {
                                val count = recipeCountMap[recipe] ?: 0
                                recipeCountMap[recipe] = count + 1
                            }
                        }
                    } else {
                        Log.d("ITM", "Request failed with status: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.d("ITM", "Network request failed", e)
                }
            }

            // 모든 요청이 완료된 후에 처리할 작업
            withContext(Dispatchers.Main) {

                Log.d("ITM", "Final things")
                val sortedRecipeData = recipeCountMap.entries.sortedByDescending { it.value }

                val recyclerView = findViewById<RecyclerView>(R.id.recipe_recycler_view)
                val adapter = IngredientRecipeAdapter(
                    sortedRecipeData.map { Pair(it.key, it.value) },
                    this@RecipeSearchWithIngredientActivityDetail
                )
                recyclerView.layoutManager = LinearLayoutManager(this@RecipeSearchWithIngredientActivityDetail)
                recyclerView.adapter = adapter

            }

            Log.d("ITM", "Finished???")
        }
    }
}