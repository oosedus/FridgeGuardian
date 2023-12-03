package com.example.fridgeguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth
        // 만약 현재 uid값이 있다면, 이미 로그인한 사람임. UID값이 없다면, 로그인 해야하는 사람

        if(auth.currentUser?.uid==null){
            Log.d("SplashActivity","null")

            Handler().postDelayed({
                startActivity(Intent(this,IntroActivity::class.java))
                finish()
            },3000)
        }else{
            Log.d("SplashActivity","not null")

            Handler().postDelayed({
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            },3000)
        }



//        Handler().postDelayed({
//            startActivity(Intent(this,IntroActivity::class.java))
//            finish()
//        },3000)

    }
}