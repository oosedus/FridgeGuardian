package com.example.fridgeguardian

import account.MyPageActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import home.HomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecipeSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_search_activity_main)

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
        initSearchView()
    }
    private fun initSearchView() {
        // init SearchView
        val searchviews = findViewById<SearchView>(R.id.search)
        searchviews.isSubmitButtonEnabled = false
        searchviews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val retrofit= Retrofit.Builder()
                    .baseUrl("http://openapi.foodsafetykorea.go.kr")
                    .addConverterFactory(GsonConverterFactory.create()) // Json데이터를 사용자가 정의한 Java 객채로 변환해주는 라이브러리
                    .build() //레트로핏 구현체 완성!

                val RecipeServiceGo = retrofit.create(RecipeGetForm::class.java) //retrofit객체 만듦!
                val editText = query
                val TargetFood = editText.toString()
                val call = RecipeServiceGo.getRecipes("57777c04fcc34d9a978b", "COOKRCP01", "json", 1, 1000, "\"${TargetFood}\"")

                call.enqueue(object : Callback<RecipeResponse> {
                    override fun onResponse(
                        call: Call<RecipeResponse>,
                        response: Response<RecipeResponse>
                    ) {
                        if (response.isSuccessful) {
                            // 요청이 성공적으로 완료되었을 때 실행되는 코드를 작성합니다.
                            // response.body()를 통해 응답 본문에 접근할 수 있습니다.
                            val recipeData = response.body()
                            Log.d("ITM", "Data received: $recipeData")

                            val recyclerView = findViewById<RecyclerView>(R.id.recipe_recycler_view)
                            val itemMargin = RecyclerviewMargin()
                            recyclerView.addItemDecoration(itemMargin)
                            val adapter = RecipeAdapter(recipeData?.cookRcp01?.rows ?: listOf(), this@RecipeSearchActivity)
                            recyclerView.layoutManager = LinearLayoutManager(this@RecipeSearchActivity)
                            recyclerView.adapter = adapter



                        } else {
                            // 요청이 실패했을 때 실행되는 코드를 작성합니다.
                            Log.d("ITM", "Request failed with status: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                        // 네트워크 요청 자체가 실패했을 때 실행되는 코드를 작성합니다.
                        Log.d("ITM", "Network request failed", t)
                    }
                })
                Log.d("ITM", "Finished")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // @TODO
                return true
            }
        })
    }

}