package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Date

class RecipeSearchWithIngredientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_search_ingredient_activity)

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

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()

        val ingredients: MutableList<Triple<String, String, Date>> = mutableListOf()
        if (currentUser != null) {

            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            Log.d("ITM", "Recipe1")

            val ingredientsref = db.collection("users").document("${currentUserEmail}").collection("ingredients")
            Log.d("ITM", "Recipe2")
            ingredientsref.get().addOnSuccessListener { result ->
                for (document in result) {
                    val documentid = document.id
                    val documentdata = document.data as Map<String, Any>
                    val name = documentdata["name"] as String?
                    val timestamp = documentdata["expDate"] as Timestamp?
                    val date = timestamp?.toDate()  // Convert Timestamp to Date

                    // !! 는 절대 null이 아니라는 걸 나타냄
                    ingredients.add(Triple(documentid, name!!, date!!))

                }

                val recyclerView = findViewById<RecyclerView>(R.id.ingredient_search_recyclerview)
                val itemMargin = RecyclerviewMargin()
                recyclerView.addItemDecoration(itemMargin)
                val adapter = IngredientSearchAdapter(ingredients, this@RecipeSearchWithIngredientActivity)
                recyclerView.layoutManager = LinearLayoutManager(this@RecipeSearchWithIngredientActivity)
                recyclerView.adapter = adapter

                val button: Button = findViewById(R.id.button)
                button.setOnClickListener {
                    val selectedIngredients = adapter.getSelectedIngredients()  // 선택된 항목의 제목 리스트 가져오기
                    val intent = Intent(this, RecipeSearchWithIngredientActivityDetail::class.java)
                    intent.putStringArrayListExtra("selectedIngredients", ArrayList(selectedIngredients))  // Intent에 담기
                    startActivity(intent)  // 다른 액티비티 시작
                }

                // 리소스에서 Divider Drawable 가져오기
                val divider = ContextCompat.getDrawable(this@RecipeSearchWithIngredientActivity, R.drawable.divider)

                divider?.let {
                    val dividerItemDecoration = DividerItemDecoration(this@RecipeSearchWithIngredientActivity, RecyclerView.VERTICAL)
                    dividerItemDecoration.setDrawable(it)
                    recyclerView.addItemDecoration(dividerItemDecoration)
                }
            }.addOnFailureListener { exception ->
                Log.d("ITM", "데이터 로드 실패: ${exception.message}")
            }

        }



    }
}