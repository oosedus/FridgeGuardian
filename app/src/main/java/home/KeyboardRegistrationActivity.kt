package home

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fridgeguardian.CommunityMainActivity
import Account.MyPageActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.ActivityKeyboardRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import home.Ingredient
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class KeyboardRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKeyboardRegistrationBinding
    private lateinit var firestore: FirebaseFirestore
    private val ingredientsList = mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyboardRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupBottomNavigationView()
        addIngredientForm()
        handleFinishButton()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Add Ingredients"
        setSupportActionBar(binding.toolbar)
    }

    private fun addIngredientForm() {
        val formView = LayoutInflater.from(this).inflate(R.layout.ingredient_registraton_form, binding.ingredientFormContainer, false)

        setupCategorySpinner(formView.findViewById(R.id.spinnerCategory))
        setupQuantitySpinner(formView.findViewById(R.id.spinnerQuantity))
        setupDatePicker(formView.findViewById(R.id.editTextExpirationDate))

        // 등록폼이 하나 남았을때는 remove 버튼 누르지 못하게 함.
        val removeButton = formView.findViewById<ImageView>(R.id.buttonRemove)
        removeButton.apply {
            setImageResource(R.drawable.ingredient_remove_button)
            // Disable the remove button only for the first form
            isClickable = binding.ingredientFormContainer.childCount > 0
            isEnabled = binding.ingredientFormContainer.childCount > 0
            visibility = if (binding.ingredientFormContainer.childCount == 0) View.INVISIBLE else View.VISIBLE
            setOnClickListener {
                binding.ingredientFormContainer.removeView(formView)

                if (binding.ingredientFormContainer.childCount == 1) {
                    binding.ingredientFormContainer.getChildAt(0).findViewById<ImageView>(R.id.buttonRemove).apply {
                        isClickable = false
                        isEnabled = false
                        visibility = View.INVISIBLE
                    }
                }
            }
        }

        formView.findViewById<ImageView>(R.id.buttonAddMore).setOnClickListener {
            addIngredientForm()
        }

        binding.ingredientFormContainer.addView(formView)
        
        if (binding.ingredientFormContainer.childCount == 1) {
            removeButton.isClickable = false
            removeButton.isEnabled = false
            removeButton.visibility = View.INVISIBLE
        }
    }

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

    // 미리 저장해둔 카테고리로 Spinner 준비
    private fun setupCategorySpinner(spinner: Spinner) {
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinner.adapter = adapter
    }

    // 미리 1~30까지 수량으로 Spinner 준비
    private fun setupQuantitySpinner(spinner: Spinner) {
        val quantities = resources.getStringArray(R.array.quantity_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quantities)
        spinner.adapter = adapter
    }

    // 유통기한은 캘린더 버튼을 누르면 DatePicker를 띄워서 선택하도록 함
    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)

                if (!selectedCalendar.after(today)) {
                    editText.setBackgroundColor(Color.RED)
                    Toast.makeText(this, "Expiration date cannot be in the past or today.", Toast.LENGTH_LONG).show()
                } else {
                    editText.tag = selectedCalendar.time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    editText.setText(dateFormat.format(selectedCalendar.time))
                    editText.setBackgroundColor(Color.TRANSPARENT)
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // Finish 버튼 눌렀을 때 만약 채워지지 않은 항목이 있으면 빨간색으로 표시
    // 다 채워졌을때만 Firestore 에 저장됨.
    private fun handleFinishButton() {
        binding.btnFinish.setOnClickListener {
            if (validateInputs()) {
                ingredientsList.clear()
                for (i in 0 until binding.ingredientFormContainer.childCount) {
                    val ingredientView = binding.ingredientFormContainer.getChildAt(i)
                    val category = ingredientView.findViewById<Spinner>(R.id.spinnerCategory).selectedItem.toString()
                    val name = ingredientView.findViewById<EditText>(R.id.editTextName).text.toString()
                    val quantityString = ingredientView.findViewById<Spinner>(R.id.spinnerQuantity).selectedItem.toString()
                    val quantity = quantityString.toIntOrNull() ?: 0

                    val expDate = ingredientView.findViewById<EditText>(R.id.editTextExpirationDate).tag as? Date

                    if (expDate != null) {
                        ingredientsList.add(Ingredient(name, category, quantity, expDate))
                    } else {
                        // Handle the case where expDate is null
                        Toast.makeText(this, "Please enter a valid expiration date for all ingredients.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
                saveIngredientsToFirestore(FirebaseAuth.getInstance().currentUser?.email ?: "", ingredientsList)
            }
        }
    }

    // 완료 후 HomeActivity 로 돌아가서 실시간 업데이트 내용 확인
    private fun saveIngredientsToFirestore(userEmail: String, ingredients: List<Ingredient>) {
        val userIngredientsCollection = firestore.collection("users").document(userEmail).collection("ingredients")

        for (ingredient in ingredients) {
            userIngredientsCollection.add(ingredient).addOnSuccessListener {
                if (ingredients.indexOf(ingredient) == ingredients.size - 1) {
                    Toast.makeText(this, "Ingredients saved successfully.", Toast.LENGTH_SHORT).show()
                    navigateBackToHome()
                }
            }
        }
    }
    private fun validateInputs(): Boolean {
        var isValid = true

        for (i in 0 until binding.ingredientFormContainer.childCount) {
            val ingredientView = binding.ingredientFormContainer.getChildAt(i)

            // Name validation
            val nameEditText = ingredientView.findViewById<EditText>(R.id.editTextName)
            if (nameEditText.text.isNullOrEmpty()) {
                nameEditText.error = "This field is required"
                isValid = false
            }

            // Category validation
            val categorySpinner = ingredientView.findViewById<Spinner>(R.id.spinnerCategory)
            if (categorySpinner.selectedItem == null) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_LONG).show()
                isValid = false
            }

            // Quantity validation
            val quantitySpinner = ingredientView.findViewById<Spinner>(R.id.spinnerQuantity)
            if (quantitySpinner.selectedItem == null || quantitySpinner.selectedItem.toString().toIntOrNull() == null) {
                Toast.makeText(this, "Please select a quantity", Toast.LENGTH_LONG).show()
                isValid = false
            }

            // Expiration date validation
            val expDateEditText = ingredientView.findViewById<EditText>(R.id.editTextExpirationDate)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val date = dateFormat.parse(expDateEditText.text.toString())
                if (date == null || date.before(Date())) {
                    expDateEditText.error = "Please enter a valid future date"
                    isValid = false
                }
            } catch (e: ParseException) {
                expDateEditText.error = "Please enter a valid date in format YYYY-MM-DD"
                isValid = false
            }
        }

        return isValid
    }

    private fun navigateBackToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

}
