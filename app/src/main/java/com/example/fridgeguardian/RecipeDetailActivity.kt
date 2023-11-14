package com.example.fridgeguardian

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_recipe_form)

        Log.d("ITM", "Recipe Detail Page")
    }
}