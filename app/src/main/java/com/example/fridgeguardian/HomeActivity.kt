package com.example.fridgeguardian

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fridgeguardian.databinding.HomeActivityMainBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
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

        binding.fabRegister.setOnClickListener {
            showRegistrationPopup(it)
        }
    }

    private fun showRegistrationPopup(anchorView: View) {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(R.layout.dialog_register_option, null)

        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
        }

        // 팝업창 내에 클릭 시 해당 액티비티로 넘어가게 설정
        val keyboardLayout = popupView.findViewById<LinearLayout>(R.id.keyboard_registration_layout)
        val voiceLayout = popupView.findViewById<LinearLayout>(R.id.voice_registration_layout)

        keyboardLayout.setOnClickListener {
            startActivity(Intent(this@HomeActivity, KeyboardRegistrationActivity::class.java))
            popupWindow.dismiss()
        }

        voiceLayout.setOnClickListener {
            startActivity(Intent(this@HomeActivity, VoiceRegistrationActivity::class.java))
            popupWindow.dismiss()
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        anchorView.post {
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)

            val x = location[0] + anchorView.width / 2 - popupView.measuredWidth / 2
            val y = location[1] - popupView.measuredHeight

            // 등록 버튼 위에 팝업 창 생기게 위치 설정
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y - 10)
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
    // 식재료가 앱 내에서 등록되었을때 실시간으로 업데이트 되도록 수정
    private fun setupRecyclerView(userEmail: String) {
        val recyclerView = binding.rvIngredients
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val collectionReference = firestore.collection("users").document(userEmail).collection("ingredients")
        collectionReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("HomeActivity", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                ingredientsList.clear()
                for (document in snapshot.documents) {
                    val ingredient = document.toObject(Ingredient::class.java)
                    ingredient?.documentId = document.id // 각 식재료 별 업데이트할때 필요한 식재료 ID
                    ingredient?.let { ingredientsList.add(it) }
                }
                ingredientsList.sortBy { it.daysUntilExpired }
                adapter.notifyDataSetChanged()
            } else {
                Log.d("HomeActivity", "Current data: null")
            }
        }
    }
}

