package com.example.fridgeguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.fridgeguardian.databinding.ActivityCommunityMainBinding
import com.google.firebase.auth.FirebaseAuth

class CommunityMainActivity : AppCompatActivity() {

  private lateinit var binding : ActivityCommunityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_main)

        binding.cClose.setOnClickListener{
            showcloseDialog()
        }
   }
    private fun showcloseDialog(){
        val dialog = CloseDialogFragment()
        dialog.show(supportFragmentManager, "CloseDialogFragment")
    }
}