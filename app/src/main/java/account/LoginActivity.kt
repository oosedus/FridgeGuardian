package account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.loginBtn.setOnClickListener {

            val email = binding.emailArea.text.toString()
            val password = binding.passwordArea.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Success to Log In",Toast.LENGTH_LONG).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)

                    } else {
                        Toast.makeText(this,"Fail to Log In",Toast.LENGTH_LONG).show()
                    }

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result // FCM 토큰
                            val firebaseAuth = FirebaseAuth.getInstance()
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null){
                                Log.d("ITM", "why")
                            }
                            val db = FirebaseFirestore.getInstance()

                            if (currentUser != null) {
                                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                                val userRef = db.collection("users").document("${currentUserEmail}")

                                // FCM 필드에 토큰 저장
                                val data = hashMapOf("token" to token)

                                userRef.set(data, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("ITM", "Token saved successfully!")
                                    }
                                    .addOnFailureListener {
                                        Log.d("ITM", "Failed to save token: ${it.message}")
                                    }
                            } else {
                                Log.d("ITM", "Current user email is null??")
                                Log.d("ITM", "Token: ${token}")
                            }
                        } else {
                            Log.d("ITM", "Failed to fetch FCM token: ${task.exception}")
                        }
                    }


                }


        }
    }
}
