package com.example.fridgeguardian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.fridgeguardian.MainActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityJoinBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.security.KeyStore.TrustedCertificateEntry

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding=DataBindingUtil.setContentView(this,R.layout.activity_join)

        binding.joinBtn.setOnClickListener {

            // isGoToJoin을 넣어서 회원가입에 필요한 정보들을 모두 입력했는지 전체적으로 확인하는거임
            // 빠지는 정보가 없어야 true임
            var isGoToJoin = true

            //사용자로부터 회원가입할 때 값 받아오기
            val name = binding.nameArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()
            val phonenumber = binding.PhoneNumberArea.text.toString()
            val nickname = binding.NickNameArea.text.toString()
            val email = binding.EmailArea.text.toString()

            //위에 회원가입을 하기 위해서 필수로 적어야하는 값들이 비었는지 안비었는지 확인하는 부분
            if(name.isEmpty()){
                Toast.makeText(this,"Please Enter Your Name",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            if(password1.isEmpty()){
                Toast.makeText(this,"Please Enter Your PassWord",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            if(password2.isEmpty()){
                Toast.makeText(this,"Please Check your PassWord",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            if(phonenumber.isEmpty()){
                Toast.makeText(this,"Please Enter Your Phone Number",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            if(nickname.isEmpty()){
                Toast.makeText(this,"Please Enter Your Nick Name",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            if(email.isEmpty()){
                Toast.makeText(this,"Please Enter Your Email",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            //비밀번호 입력칸에 적은 비밀번호랑 비밀번호 확인 칸에 적은 비밀번호랑 동일한지 아닌지를 확인하는 부분
            if(!password1.equals(password2)){
                Toast.makeText(this,"Please Confirm Your PassWord",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            //비밀번호가 6자리보다 작으면 비밀번호를 6자리 이상으로 입력하게 함
            if(password1.length<6){
                Toast.makeText(this,"Please enter a password of at least 6 characters",Toast.LENGTH_LONG).show()
                isGoToJoin=false
            }

            //isGoToJoin이 true면 이제 회원가입을 성공시키는 부분임
            if(isGoToJoin){
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Success to sign up",Toast.LENGTH_LONG).show()

                            //회원가입 성공하면 MainActivity로 넘어가게 하는거
                            val intent = Intent(this,MainActivity::class.java)

                            //근데 회원가입 성공 -> mainactivity로 넘어가 -> 뒤로가기 버튼 눌러 = 회원가입 화면인데,
                            //이를 회원가입 성공 -> mainactivity로 넘어가 -> 뒤로가기 버튼 눌러 = 앱 종료 가 되게 하는 부분임 아래는
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)

                        } else {

                            Toast.makeText(this,"Fail to sign up",Toast.LENGTH_LONG).show()

                        }
                    }
            }

        }

        // Initialize Firebase Auth
        auth = Firebase.auth

    }

}