package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val ingredientsList = arrayListOf<Ingredient>()
    private lateinit var adapter: IngredientAdapter
    private var isAscending: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Hello,\nCheck your expiration date"
        setSupportActionBar(toolbar)

        setupBottomNavigationView()

        adapter = IngredientAdapter(ingredientsList)
        val recyclerView: RecyclerView = findViewById(R.id.rvIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        if (currentUserEmail != null) {
            setupRecyclerView(currentUserEmail)
            Log.d("ITM", "$currentUserEmail")
        }

    }

    private fun setupBottomNavigationView() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.navigation)
        bottomNav.setOnItemSelectedListener { item ->
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

    private fun setupRecyclerView(userEmail: String) {
        val recyclerView: RecyclerView = findViewById(R.id.rvIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter // Use the class-level adapter

        firestore.collection("users").document(userEmail).collection("ingredients")
            .get()
            .addOnSuccessListener { documents ->
                ingredientsList.clear() // Clear the class-level list
                for (document in documents) {
                    val ingredient = document.toObject(Ingredient::class.java)
                    ingredientsList.add(ingredient) // Add to the class-level list
                }
                ingredientsList.sortBy { it.daysUntilExpired } // Initial sort
                adapter.notifyDataSetChanged() // Notify the class-level adapter of data changes
            }
            .addOnFailureListener { exception ->
                Log.w("HomeActivity", "Error getting documents: ", exception)
            }
    }
}

