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
        Log.d("ITM", "${recipe}")
        val currentUser = FirebaseAuth.getInstance().currentUser?.email
        Log.d("ITM", "${currentUser}")

        val btnPlusBookmark = findViewById<Button>(R.id.bookmark)
        btnPlusBookmark.setOnClickListener {
            if (currentUser != null) {
                val recipeName = recipe.RCP_NM
                val recipeData = hashMapOf(
                    "일련번호" to recipe.RCP_SEQ,
                    "조리방법" to recipe.RCP_WAY2,
                    "열량" to recipe.INFO_ENG,
                    "탄수화물" to recipe.INFO_CAR,
                    "단백질" to recipe.INFO_PRO,
                    "지방" to recipe.INFO_FAT,
                    "나트륨" to recipe.INFO_NA,
                    "해쉬태그" to recipe.HASH_TAG,
                    "이미지경로" to recipe.ATT_FILE_NO_MK,
                    "재료정보" to recipe.RCP_PARTS_DTLS,
                    "만드는법_01" to recipe.MANUAL01,
                    "만드는법_02" to recipe.MANUAL02,
                    "만드는법_03" to recipe.MANUAL03,
                    "만드는법_04" to recipe.MANUAL04,
                    "만드는법_05" to recipe.MANUAL05,
                    "만드는법_06" to recipe.MANUAL06,
                    "만드는법_07" to recipe.MANUAL07,
                    "만드는법_08" to recipe.MANUAL08,
                    "만드는법_09" to recipe.MANUAL09,
                    "만드는법_10" to recipe.MANUAL10,
                    "만드는법_11" to recipe.MANUAL11,
                    "만드는법_12" to recipe.MANUAL12,
                    "만드는법_13" to recipe.MANUAL13,
                    "만드는법_14" to recipe.MANUAL14,
                    "만드는법_15" to recipe.MANUAL15,
                    "만드는법_16" to recipe.MANUAL16,
                    "만드는법_17" to recipe.MANUAL17,
                    "만드는법_18" to recipe.MANUAL18,
                    "만드는법_19" to recipe.MANUAL19,
                    "만드는법_20" to recipe.MANUAL20,
                    "저감 조리법 TIP" to recipe.RCP_NA_TIP,
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