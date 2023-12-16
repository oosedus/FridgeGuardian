package account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.ActivityMyPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater) // Initialize binding
        setContentView(binding.root)

        auth = Firebase.auth

        binding.toolbar.title = "My Page"
        setSupportActionBar(binding.toolbar)

        setupBottomNavigationView()

        binding.btnProfileEdit.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
            binding.btnDeleteAccount.visibility = View.GONE
            // Navigate to Profile Edit Fragment
            val fragment = ProfileEditFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut() // Firebase 로그아웃

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.btnLogout.visibility = View.VISIBLE
                binding.btnDeleteAccount.visibility = View.VISIBLE
            }
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmation()
        }

        displayUserInfo()
    }

    override fun onResume() {
        super.onResume()
        // Refresh user info when returning to the activity
        displayUserInfo()
    }

    private fun displayUserInfo() {
        // Retrieve and display user info from Firebase and RoomDB
        val user = auth.currentUser
        binding.tvEmail.text = user?.email // Firebase

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val userInfo = user?.email?.let { db.userDao().getUserByEmail(it) }
            userInfo?.let {
                // Update UI with RoomDB data
                runOnUiThread {
                    binding.tvName.text = it.name
                    binding.tvNickname.text = it.nickname
                    binding.tvPhoneNumber.text = it.phoneNumber
                }
            }
        }
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            // Delete from Firestore first
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(firebaseUser.email!!)
                .delete()
                .addOnSuccessListener {
                    // Now delete from FirebaseAuth
                    firebaseUser.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User deleted successfully, now you can delete local data
                                deleteLocalUserData(firebaseUser.email!!)
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete user: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete user data from Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun deleteLocalUserData(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Perform Room database deletion operations here
            val db = AppDatabase.getDatabase(applicationContext)
            db.userDao().deleteUserByEmail(email)
            // Navigate back to login screen on UI thread
            withContext(Dispatchers.Main) {
                navigateToLoginScreen()
            }
        }
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This cannot be undone.")
            .setPositiveButton("Delete") { dialog, which ->
                deleteUserAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToLoginScreen() {
        val splashIntent = Intent(this, SplashActivity::class.java)
        startActivity(splashIntent)
        finish()
    }

    private fun setupBottomNavigationView() {
        binding.navigation.selectedItemId = R.id.nav_mypage
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
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
}