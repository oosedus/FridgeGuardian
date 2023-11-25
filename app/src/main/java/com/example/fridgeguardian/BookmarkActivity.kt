package com.example.fridgeguardian

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class BookmarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmark_activity_main)

        Log.d("ITM", "Proceed22")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Bookmark"
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

        Log.d("ITM", "Proceed0")
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        val db = FirebaseFirestore.getInstance()

        val bookmarksList: MutableList<Pair<String, Any>> = mutableListOf()

        if (currentUser != null) {
            Log.d("ITM", "Proceed")
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            Log.d("ITM", "${currentUserEmail}")
            val bookmarksRef = db.collection("users").document("${currentUserEmail}").collection("Bookmarks")
            Log.d("ITM", "Proceed2")
            bookmarksRef.get().addOnSuccessListener { result ->
                Log.d("ITM", "succeess")
                for (document in result) {
                    val bookmark = document.id
                    val bookmarkValue = document.data as Map<String, Any>

                    Log.d("ITM", "succeess2")
                    bookmarksList.add(Pair(bookmark, bookmarkValue))

                }
            }.addOnFailureListener { exception ->
                Log.d("ITM", "데이터 로드 실패: ${exception.message}")
            }
        }

        // 임시 버튼, 버튼 눌렸을 때 정보 알려주는
        // for을 이용해서 bookmark 정보 전부 다 알려줌
        val tempButton = findViewById<Button>(R.id.button3)
        tempButton.setOnClickListener {
            Log.d("ITM", "Button clicked")
            for (bookmark in bookmarksList) {
                Log.d("ITM", "Button clicked")
                Log.d("ITM", "북마크: ${bookmark.first}, 값: ${bookmark.second}")
            }
            val bookmark = bookmarksList[0]
            Log.d("ITM", "bookmark.second type: ${bookmark.second::class.java}")

        }

        val recyclerView = findViewById<RecyclerView>(R.id.bookmark_recycler_view)
        val adapter = BookmarkAdapter(bookmarksList, this@BookmarkActivity)
        recyclerView.layoutManager = LinearLayoutManager(this@BookmarkActivity)
        recyclerView.adapter = adapter

    }
}
