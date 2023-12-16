package home


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fridgeguardian.CommunityActivity
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.MyPageActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.ActivityIngredientEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class IngredientEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIngredientEditBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var ingredient: Ingredient
    private lateinit var ingredientId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.toolbar.title = "Modify Your Ingredient"
        setSupportActionBar(binding.toolbar)

        setupBottomNavigationView()

        // Retrieve the ingredient object and its ID passed from HomeActivity
        ingredient = intent.getSerializableExtra("ingredient") as Ingredient
        ingredientId = intent.getStringExtra("ingredientId") ?: ""

        populateFields(ingredient)

        binding.btnRemove.setOnClickListener {
            confirmAndDeleteIngredient()
        }

        binding.btnFinish.setOnClickListener {
            if (validateInputs()) {
                saveEditedIngredient()
            }
        }


    }

    // 처음에는 원래 식재료 정보로 배치되어 있음
    private fun populateFields(ingredient: Ingredient) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.editTextName.setText(ingredient.name)
        binding.editTextExpirationDate.setText(sdf.format(ingredient.expDate ?: Date()))

        // Populate the category spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategory.adapter = categoryAdapter
        val categoryPosition = categoryAdapter.getPosition(ingredient.category)
        binding.spinnerCategory.setSelection(categoryPosition)

        val quantityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.quantity_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerQuantity.adapter = quantityAdapter
        val quantityPosition = quantityAdapter.getPosition(ingredient.quantity.toString())
        binding.spinnerQuantity.setSelection(quantityPosition)
        binding.editTextExpirationDate.setText(sdf.format(ingredient.expDate ?: Date()))
        setupDatePicker(binding.editTextExpirationDate)
    }

    // DELTE 누르면 확인 POPUP 이후 Firestore 에서 정보 삭제
    private fun confirmAndDeleteIngredient() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Do you really want to delete this ingredient?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                deleteIngredientFromFirestore()
            }
            .show()
    }

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

    private fun deleteIngredientFromFirestore() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        firestore.collection("users").document(userEmail).collection("ingredients")
            .document(ingredientId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Ingredient deleted successfully", Toast.LENGTH_SHORT).show()
                navigateBackToHome()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete ingredient", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Name validation
        if (binding.editTextName.text.isNullOrEmpty()) {
            binding.editTextName.error = "This field is required"
            isValid = false
        }

        // Category validation
        if (binding.spinnerCategory.selectedItem == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Quantity validation
        if (binding.spinnerQuantity.selectedItem == null || binding.spinnerQuantity.selectedItem.toString().toIntOrNull() == null) {
            Toast.makeText(this, "Please select a quantity", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Expiration date validation
        val expDateEditText = binding.editTextExpirationDate
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

        return isValid
    }

    private fun saveEditedIngredient() {
        val updatedIngredient = Ingredient(
            binding.editTextName.text.toString(),
            binding.spinnerCategory.selectedItem.toString(),
            binding.spinnerQuantity.selectedItem.toString().toInt(),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(binding.editTextExpirationDate.text.toString())
        )

        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        firestore.collection("users").document(userEmail).collection("ingredients")
            .document(ingredientId)
            .set(updatedIngredient)
            .addOnSuccessListener {
                Toast.makeText(this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show()
                navigateBackToHome()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show()
            }
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

    private fun navigateBackToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}