package account

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        binding.joinBtn.setOnClickListener {
            val name = binding.nameArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()
            val phonenumber = binding.PhoneNumberArea.text.toString()
            val nickname = binding.NickNameArea.text.toString()
            val email = binding.EmailArea.text.toString()

            // Validate input fields
            if (validateInputs(name, email, password1, password2, nickname, phonenumber)) {
                // Proceed with Firebase registration
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // User registration successful, save additional details to Room and Firestore
                            val user = User(email, name, nickname, phonenumber)
                            saveUserToLocalDatabase(user)
                            saveUserToFirestore(email)
                            goToHomeScreen()
                        } else {
                            // User registration failed
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun validateInputs(name: String, email: String, password1: String, password2: String, nickname: String, phoneNumber: String): Boolean {
        if(name.isEmpty()){
            Toast.makeText(this,"Please Enter Your Name",Toast.LENGTH_LONG).show()
            return false
        }

        if(password1.isEmpty()){
            Toast.makeText(this,"Please Enter Your PassWord",Toast.LENGTH_LONG).show()
            return false
        }

        if(password2.isEmpty()){
            Toast.makeText(this,"Please Check your PassWord",Toast.LENGTH_LONG).show()
            return false
        }

        if(phoneNumber.isEmpty()){
            Toast.makeText(this,"Please Enter Your Phone Number",Toast.LENGTH_LONG).show()
            return false
        }

        if(nickname.isEmpty()){
            Toast.makeText(this,"Please Enter Your Nick Name",Toast.LENGTH_LONG).show()
            return false
        }

        if(email.isEmpty()){
            Toast.makeText(this,"Please Enter Your Email",Toast.LENGTH_LONG).show()
            return false
        }

        //비밀번호 입력칸에 적은 비밀번호랑 비밀번호 확인 칸에 적은 비밀번호랑 동일한지 아닌지를 확인하는 부분
        if(!password1.equals(password2)){
            Toast.makeText(this,"Please Confirm Your PassWord",Toast.LENGTH_LONG).show()
            return false
        }

        //비밀번호가 6자리보다 작으면 비밀번호를 6자리 이상으로 입력하게 함
        if(password1.length<6){
            Toast.makeText(this,"Please enter a password of at least 6 characters",Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun saveUserToLocalDatabase(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            db.userDao().insertUser(user)
        }
    }

    private fun saveUserToFirestore(email: String) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = hashMapOf(
            "email" to email
        )

        db.collection("users").document(email).set(userDoc)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved to Firestore", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user to Firestore: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}