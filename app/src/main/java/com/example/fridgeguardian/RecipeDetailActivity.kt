package com.example.fridgeguardian

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ITM", "RecipeDetailActivity success")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_recipe_form)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val recipe = intent.getSerializableExtra("recipe") as RecipeDataForm
        val currentUser = FirebaseAuth.getInstance().currentUser?.email
        Log.d("ITM", "${currentUser}")

        val btnPlusBookmark = findViewById<Button>(R.id.bookmark)
        btnPlusBookmark.setOnClickListener {
            if (currentUser != null) {
                val recipeName = recipe.RCP_NM
                val recipeData = hashMapOf(
                    "Name" to recipe.ATT_FILE_NO_MK,
                    "Hashtag" to recipe.HASH_TAG,
                    "carbohydrate" to recipe.INFO_CAR,
                    "Nat" to recipe.INFO_NA,
                    "Calory" to recipe.INFO_FAT
                )

                val userBookmarkRef = firestore.collection("users")
                    .document("${currentUser}")
                    .collection("Bookmarks")
                    .document("${recipeName}")

                userBookmarkRef.set(recipeData)
                    .addOnSuccessListener {
                        Log.d("ITM", "Recipe information successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ITM", "Error writing recipe document", e)
                    }
            } else {
                Log.e("ITM", "Current user is null")
            }
        }
    }
}