package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val retrofit= Retrofit.Builder()
            .baseUrl("http://openapi.foodsafetykorea.go.kr")
            .addConverterFactory(GsonConverterFactory.create()) // Json데이터를 사용자가 정의한 Java 객채로 변환해주는 라이브러리
            .build() //레트로핏 구현체 완성!

        val RecipeServiceGo = retrofit.create(RecipeGetForm::class.java) //retrofit객체 만듦!

        val Searchbutton = findViewById<Button>(R.id.button)
        Searchbutton.setOnClickListener{
            val editText = findViewById<EditText>(R.id.SearchText)
            val TargetFood = editText.text.toString()
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
        }



    }
}