package home

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fridgeguardian.CommunityMainActivity
import account.MyPageActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.HomeActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val ingredientsList = arrayListOf<Ingredient>()
    private lateinit var adapter: IngredientAdapter
    private var isAscending: Boolean = true

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeDetector: ShakeDetector? = null

    private var filteredIngredientsList = arrayListOf<Ingredient>()

    // binding 으로 코드 수정
    private lateinit var binding: HomeActivityMainBinding

    companion object {
        const val PREFS_NAME = "ShakeFeaturePrefs"
        const val SHAKE_FEATURE_KEY = "ShakeFeature"
    }

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

        setupCategorySpinner()

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

        // 센서 관련 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector {
            toggleSortOrder()
        }
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)

        // SharedPreferences 초기화 및 토글 상태 설정
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        binding.toggleShake.isChecked = sharedPrefs.getBoolean(SHAKE_FEATURE_KEY, false)
        binding.toggleShake.setOnCheckedChangeListener { _, isChecked ->
            handleShakeFeatureToggle(isChecked)
        }
    }

    private fun toggleSortOrder() {
        isAscending = !isAscending
        adapter.sortIngredients(isAscending)
    }


    private fun setupCategorySpinner() {
        // 기존 카테고리 목록에 "All" 옵션 추가
        val categories = listOf("All") + resources.getStringArray(R.array.category_array)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position) as String
                filterIngredientsByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterIngredientsByCategory("All")
            }
        }
    }

    private fun filterIngredientsByCategory(category: String) {
        val filteredList = if (category == "All") {
            ingredientsList
        } else {
            ingredientsList.filter { it.category == category }
        }
        adapter.updateData(ArrayList(filteredList))
        adapter.sortIngredients(isAscending)
        updateNoIngredientsText(filteredList.isEmpty())
    }

    private fun updateIngredientsList(newIngredients: List<Ingredient>) {
        ingredientsList.clear()
        ingredientsList.addAll(newIngredients)
        val currentCategory = binding.spinnerCategory.selectedItem as? String ?: "All"
        filterIngredientsByCategory(currentCategory)
        updateNoIngredientsText(newIngredients.isEmpty())
    }

    private fun updateNoIngredientsText(isEmpty: Boolean) {
        binding.tvNoIngredients.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }



    // ToggleButton 상태 처리
    private fun handleShakeFeatureToggle(isChecked: Boolean) {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(SHAKE_FEATURE_KEY, isChecked)
            apply()
        }

        if (isChecked) {
            Toast.makeText(this, "Shake to sort ON", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Shake to sort OFF", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(shakeDetector)
    }
    // 흔들림 감지 클래스
    class ShakeDetector(val onShake: () -> Unit) : SensorEventListener {
        private var lastUpdate: Long = 0
        private var lastShakeTimestamp: Long = 0
        private var lastX: Float = 0.0f
        private var lastY: Float = 0.0f
        private var lastZ: Float = 0.0f
        private var shakeCount: Int = 0

        private val SHAKE_THRESHOLD = 500
        private val SHAKE_SLOP_TIME_MS = 500
        private val SHAKE_COUNT_RESET_TIME_MS = 3000
        private val SHAKE_COUNT_THRESHOLD = 2

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > SHAKE_SLOP_TIME_MS) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    if (curTime - lastShakeTimestamp > SHAKE_COUNT_RESET_TIME_MS) {
                        shakeCount = 0
                    }

                    lastShakeTimestamp = curTime
                    shakeCount++

                    if (shakeCount >= SHAKE_COUNT_THRESHOLD) {
                        onShake()
                        shakeCount = 0
                    }
                }

                lastX = x
                lastY = y
                lastZ = z
            }
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
        binding.navigation.selectedItemId = R.id.nav_home
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_recipe -> {
                    startActivity(Intent(this, RecipeActivity::class.java))
                    true
                }

                R.id.nav_community -> {
                    startActivity(Intent(this, CommunityMainActivity::class.java))
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
                val newIngredients = snapshot.documents.mapNotNull { document ->
                    document.toObject(Ingredient::class.java)?.apply {
                        documentId = document.id
                    }
                }
                updateIngredientsList(newIngredients)
            } else {
                Log.d("HomeActivity", "Current data: null")
            }
        }
    }
}