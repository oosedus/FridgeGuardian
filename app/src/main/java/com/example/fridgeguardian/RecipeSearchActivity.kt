package com.example.fridgeguardian

import Account.MyPageActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
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

import com.google.firebase.firestore.FirebaseFirestore

class RecipeSearchActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_search_activity_main)
        firestore = FirebaseFirestore.getInstance()

        firestore.collection("TotalBookmark")
            .get()
            .addOnSuccessListener { documents ->
                val top3Map = mutableMapOf<String, Int>()

                // 문서들을 순회하면서 개수 필드를 확인합니다.
                for (document in documents) {
                    val count = document.getLong("개수")?.toInt() ?: 0
                    val recipeName = document.id

                    // 개수와 recipeName을 Map에 저장합니다.
                    top3Map[recipeName] = count
                }

                // Map을 내림차순으로 정렬하고 상위 3개를 가져옵니다.
                val sortedTop3 = top3Map.toList().sortedByDescending { (_, value) -> value }.take(3)

                var rank1: Pair<String, Int>? = null
                var rank2: Pair<String, Int>? = null
                var rank3: Pair<String, Int>? = null

                for ((index, pair) in sortedTop3.withIndex()) {
                    val (name, count) = pair // 상위 3개의 데이터

                    when (index) {
                        0 -> rank1 = Pair(name, count) // 첫 번째 데이터를 rank1에 저장
                        1 -> rank2 = Pair(name, count) // 두 번째 데이터를 rank2에 저장
                        2 -> rank3 = Pair(name, count) // 세 번째 데이터를 rank3에 저장
                    }
                }
                val rank1show = findViewById<TextView>(R.id.First)
                val rank2show = findViewById<TextView>(R.id.Second)
                val rank3show = findViewById<TextView>(R.id.Third)
                rank1?.let { (name, count) ->
                    val textToShow = "Rank 1: $name : $count"
                    rank1show.text = textToShow
                }
                rank2?.let { (name, count) ->
                    val textToShow = "Rank 2: $name : $count"
                    rank2show.text = textToShow
                }
                rank3?.let { (name, count) ->
                    val textToShow = "Rank 3: $name : $count"
                    rank3show.text = textToShow
                }
            }.addOnFailureListener { exception ->
                Log.d("ITM", "Error getting documents: ${exception}")
            }


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