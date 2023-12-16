package setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.fridgeguardian.R
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val logoutBtn:Button = findViewById(R.id.c_logoutBtn)
        logoutBtn.setOnClickListener{

            auth.signOut()

            Toast.makeText(this,"Log out",Toast.LENGTH_LONG).show()

        }


    }
}