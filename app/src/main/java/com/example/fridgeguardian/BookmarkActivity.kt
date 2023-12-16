package com.example.fridgeguardian

import account.MyPageActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import home.HomeActivity

class BookmarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookmark_activity_main)

        Log.d("ITM", "Proceed22")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Bookmark"
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
                val recyclerView = findViewById<RecyclerView>(R.id.bookmark_recycler_view)
                val itemMargin = RecyclerviewMargin()
                recyclerView.addItemDecoration(itemMargin)
                val adapter = BookmarkAdapter(bookmarksList, this@BookmarkActivity)
                recyclerView.layoutManager = LinearLayoutManager(this@BookmarkActivity)
                recyclerView.adapter = adapter

                // 리소스에서 Divider Drawable 가져오기
                val divider = ContextCompat.getDrawable(this@BookmarkActivity, R.drawable.divider)

                divider?.let {
                    val dividerItemDecoration = DividerItemDecoration(this@BookmarkActivity, RecyclerView.VERTICAL)
                    dividerItemDecoration.setDrawable(it)
                    recyclerView.addItemDecoration(dividerItemDecoration)
                }



                Log.d("ITM", "finished")
            }.addOnFailureListener { exception ->
                Log.d("ITM", "데이터 로드 실패: ${exception.message}")
            }
        }
    }
}
