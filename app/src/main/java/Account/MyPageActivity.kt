package Account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import com.example.fridgeguardian.databinding.ActivityMyPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

//        binding.btnProfileEdit.setOnClickListener {
//            findNavController().navigate(R.id.action_myPageFragment_to_profileEditFragment)
//        }

        binding.btnLogout.setOnClickListener {
            auth.signOut() // Firebase 로그아웃

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

//        binding.btnDeleteAccount.setOnClickListener {
//            // Delete Firebase user and related data in RoomDB
//            deleteAccount()
//        }

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

//    private fun deleteAccount() {
//        val user = auth.currentUser
//        user?.delete()?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // Delete user data from RoomDB
//                lifecycleScope.launch(Dispatchers.IO) {
//                    val db = AppDatabase.getDatabase(applicationContext)
//                    user.email?.let { db.userDao().deleteUserByEmail(it) }
//                    // Redirect to login after deletion from RoomDB
//                    withContext(Dispatchers.Main) {
//                        val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//            }
//        }?.addOnFailureListener {
//            // Handle failure, log the error or notify the user
//            Toast.makeText(this, "Failed to delete account: ${it.message}", Toast.LENGTH_SHORT).show()
//        }
//    }

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


