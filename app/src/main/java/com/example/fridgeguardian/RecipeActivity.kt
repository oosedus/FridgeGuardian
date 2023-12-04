package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import home.HomeActivity

class RecipeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_activity_main)

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
                    val intent = Intent(this, RecipeActivity::class.java)
                    startActivity(intent)
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

        val searchButton = findViewById<Button>(R.id.rectangle1)
        searchButton.setOnClickListener {
            goToRecipeSearchScreen()
        }

        val searchButton2 = findViewById<Button>(R.id.rectangle3)
        searchButton2.setOnClickListener {
            goToBookmarkScreen()
        }

    }
    private fun goToRecipeSearchScreen() {
        val intent = Intent(this, RecipeSearchActivity::class.java)
        startActivity(intent)

    }

    private fun goToBookmarkScreen() {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)

    }


}