package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeguardian.databinding.HomeActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val ingredientsList = arrayListOf<Ingredient>()
    private lateinit var adapter: IngredientAdapter
    private var isAscending: Boolean = true

    // binding 으로 코드 수정
    private lateinit var binding: HomeActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = HomeActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        binding.toolbar.title = "Hello,\nCheck your expiration date"
        setSupportActionBar(binding.toolbar)

        setupBottomNavigationView()

        adapter = IngredientAdapter(ingredientsList)
        binding.rvIngredients.layoutManager = LinearLayoutManager(this)
        binding.rvIngredients.adapter = adapter

        if (currentUserEmail != null) {
            setupRecyclerView(currentUserEmail)
            Log.d("ITM", "$currentUserEmail")
        }

        binding.sortButton.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                isAscending = !isAscending
                adapter.sortIngredients(isAscending)
            }
        }

    }

    // 네비게이션 보여주는 것 함수로 간단하게 바꿈
    private fun setupBottomNavigationView() {
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_recipe -> {
                    startActivity(Intent(this, RecipeActivity::class.java))
                    true
                }
                R.id.nav_community -> {
                    startActivity(Intent(this, CommunityActivity::class.java))
                    true
                }
                R.id.nav_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Firestore에 있는 식재료 가져와서 보여주기(RecyclerView 사용)
    private fun setupRecyclerView(userEmail: String) {
        val recyclerView = binding.rvIngredients
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        firestore.collection("users").document(userEmail).collection("ingredients")
            .get()
            .addOnSuccessListener { documents ->
                ingredientsList.clear()
                for (document in documents) {
                    val ingredient = document.toObject(Ingredient::class.java)
                    ingredientsList.add(ingredient)
                }
                ingredientsList.sortBy { it.daysUntilExpired } // 처음에는 오름차순 정렬로 설정
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error getting documents: ", exception)
            }
    }
}

