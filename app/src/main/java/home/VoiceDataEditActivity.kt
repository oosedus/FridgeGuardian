package home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.MyPageActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.ActivityVoiceDataEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class VoiceDataEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceDataEditBinding
    private lateinit var firestore: FirebaseFirestore
    private val ingredientsList = mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceDataEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupBottomNavigationView()
        handleIntentData()
        handleFinishButton()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Add Ingredients"
        setSupportActionBar(binding.toolbar)
    }

    private fun setupBottomNavigationView() {
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

    private fun handleIntentData() {
        val parsedIngredients = intent.getSerializableExtra("parsedIngredients") as? ArrayList<Map<String, Any>>
        parsedIngredients?.forEach { ingredientMap ->
            addIngredientForm(ingredientMap)
        }
    }

    private fun addIngredientForm(ingredientData: Map<String, Any>) {
        val formView = LayoutInflater.from(this).inflate(R.layout.ingredient_registraton_form, binding.ingredientFormContainer, false)

        val nameEditText = formView.findViewById<EditText>(R.id.editTextName)
        val categorySpinner = formView.findViewById<Spinner>(R.id.spinnerCategory)
        val quantitySpinner = formView.findViewById<Spinner>(R.id.spinnerQuantity)
        val expDateEditText = formView.findViewById<EditText>(R.id.editTextExpirationDate)

        setupCategorySpinner(categorySpinner)
        setupQuantitySpinner(quantitySpinner)
        setupDatePicker(expDateEditText)

        nameEditText.setText(ingredientData["name"] as? String)
        setSpinnerSelection(categorySpinner, ingredientData["category"] as? String)
        setSpinnerSelection(quantitySpinner, (ingredientData["quantity"] as? Int)?.toString())
        expDateEditText.setText(ingredientData["expDate"] as? String)

        binding.ingredientFormContainer.addView(formView)
    }

    private fun setupCategorySpinner(spinner: Spinner) {
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinner.adapter = adapter
    }

    private fun setupQuantitySpinner(spinner: Spinner) {
        val quantities = resources.getStringArray(R.array.quantity_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quantities)
        spinner.adapter = adapter
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText(dateFormat.format(selectedCalendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(value)
        spinner.setSelection(position, true)
    }

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
                    val expDateEditText = ingredientView.findViewById<EditText>(R.id.editTextExpirationDate)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val expDate = dateFormat.parse(expDateEditText.text.toString())

                    ingredientsList.add(Ingredient(name, category, quantity, expDate))
                }
                saveIngredientsToFirestore(FirebaseAuth.getInstance().currentUser?.email ?: "", ingredientsList)
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

    private fun navigateBackToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}