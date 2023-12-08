package com.example.fridgeguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            goToMainScreen()
        }

        // 회원가입 기능
        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val email = findViewById<EditText>(R.id.email)
            val password = findViewById<EditText>(R.id.password)

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
                        saveUserToFirestore(email.text.toString())

                    } else {
                        Toast.makeText(this, "실패 ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }

                }
        }

        // 로그인 기능
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {

            val email = findViewById<EditText>(R.id.email)
            val password = findViewById<EditText>(R.id.password)

            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                        goToMainScreen()
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun saveUserToFirestore(userEmail: String) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = hashMapOf(
            "email" to userEmail
        )

        auth.currentUser?.let { user ->
            db.collection("users").document(userEmail) // 이메일을 문서 ID로 사용
                .set(userDoc)
                .addOnSuccessListener {
                    // 성공적으로 저장된 경우
                    Toast.makeText(this, "Firestore에 저장되었습니다.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // 저장 실패한 경우
                    Log.d("ITM", "Firestore 저장 실패: ${e.message}")
                    Toast.makeText(this, "Firestore 저장 실패: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun goToMainScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}