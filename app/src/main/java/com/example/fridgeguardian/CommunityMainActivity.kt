package com.example.fridgeguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.fridgeguardian.databinding.ActivityCommunityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import setting.SettingActivity

class CommunityMainActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

  private lateinit var binding : ActivityCommunityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        auth= Firebase.auth

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_main)

        binding.cClose.setOnClickListener{
            showcloseDialog()
        }

        findViewById<ImageView>(R.id.c_settingBtn).setOnClickListener{
            val intent = Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }
   }
    private fun showcloseDialog(){
        val dialog = CloseDialogFragment()
        dialog.show(supportFragmentManager, "CloseDialogFragment")
    }
}