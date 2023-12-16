package com.example.fridgeguardian

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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


        val recipeName = recipe.RCP_NM
        val recipeNameTextView = findViewById<TextView>(R.id.RecipeName)
        recipeNameTextView.setText(recipeName)


        val image = findViewById<ImageView>(R.id.imageoffood)
        val options = com.bumptech.glide.request.RequestOptions()
            .placeholder(R.drawable.loading) // 로드 중에 표시할 이미지
            .error(R.drawable.error) // 에러 발생 시 표시할 이미지

        val imageurl = recipe.ATT_FILE_NO_MK.toString()

        Glide.with(this)
            .load(imageurl)
            .apply(options)
            .into(image)


        val informations : String = "열량 : ${recipe.INFO_ENG}, 탄수화물 : ${recipe.INFO_CAR}, 단백질 : ${recipe.INFO_PRO}. 지방 : ${recipe.INFO_FAT}, 나트륨 : ${recipe.INFO_NA}"
        val otherinformation = findViewById<TextView>(R.id.information)
        otherinformation.setText(informations)



        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        val adapter = RecipeDetailFinalAdapter(recipe, this@RecipeDetailActivity)
        recyclerView.layoutManager = LinearLayoutManager(this@RecipeDetailActivity)
        recyclerView.adapter = adapter





        // bookmark 관련
        val btnPlusBookmark = findViewById<Button>(R.id.bookmark)

        if (currentUser != null) {
            val userBookmarkRef = firestore.collection("users")
                .document("${currentUser}")
                .collection("Bookmarks")
                .document("${recipeName}")

            // 액티비티가 생성될 때 북마크 상태 확인
            userBookmarkRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // 북마크가 존재하면 버튼을 ON 상태로 설정
                        btnPlusBookmark.text = "북마크 저장"
                    } else {
                        // 북마크가 존재하지 않으면 버튼을 OFF 상태로 설정
                        btnPlusBookmark.text = "북마크 저장X"
                    }
                } else {
                    Log.e("ITM", "Error getting documents: ", task.exception)
                }
            }
        }

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

                val bookmarkCountRef = firestore.collection("TotalBookmark").document("${recipeName}")

                userBookmarkRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            // 북마크가 이미 존재하면 삭제
                            userBookmarkRef.delete()
                            btnPlusBookmark.text = "북마크 저장X" // 버튼 텍스트를 'OFF'로 변경
                            Log.d("ITM", "감소1")

                            // 북마크 개수 감소
                            firestore.runTransaction { transaction ->
                                val bookmark = transaction.get(bookmarkCountRef)
                                val newCount = bookmark.getLong("개수")!! - 1
                                if (newCount <= 0) {
                                    transaction.delete(bookmarkCountRef)
                                } else {
                                    transaction.update(bookmarkCountRef, "개수", newCount)
                                }
                            }.addOnFailureListener { e ->
                                Log.e("ITM", "Error in transaction", e)
                            }

                        } else {
                            // 북마크가 존재하지 않으면 추가
                            userBookmarkRef.set(recipeData)
                            btnPlusBookmark.text = "북마크 저장" // 버튼 텍스트를 'ON'로 변경
                            Log.d("ITM", "증가1")

                            // 북마크 개수 증가
                            firestore.runTransaction { transaction ->
                                val bookmark = transaction.get(bookmarkCountRef)
                                if (!bookmark.exists()) {
                                    transaction.set(bookmarkCountRef, hashMapOf("개수" to 1))
                                } else {
                                    val newCount = bookmark.getLong("개수")!! + 1
                                    transaction.update(bookmarkCountRef, "개수", newCount)
                                }
                            }.addOnFailureListener { e ->
                                Log.e("ITM", "Error in transaction", e)
                            }

                        }
                    } else {
                        Log.e("ITM", "Error getting documents: ", task.exception)
                    }
                }
            }
        }
    }
}