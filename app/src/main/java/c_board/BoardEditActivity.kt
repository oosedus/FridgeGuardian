package c_board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityBoardEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import utils.FBAuth
import utils.FBRef

class BoardEditActivity : AppCompatActivity() {

    private lateinit var key: String

    private lateinit var binding : ActivityBoardEditBinding

    private val TAG = BoardEditActivity::class.java

    private lateinit var writerUid : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_edit)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)

        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)
//
       binding.cEditBtn.setOnClickListener{
           editBoardData(key)
      }


    }

    private fun editBoardData(key: String){
        FBRef.boardRef
            .child(key)
            .setValue(
                BoardModel(binding.cTitleAreaRname.text.toString(),
               binding.cContentRcontent.text.toString(),
                binding.cContentRingredient.text.toString(),
                    writerUid,
                FBAuth.getTime())

            )

        Toast.makeText(this,"Success to edit",Toast.LENGTH_LONG).show()

        finish()

    }


    private fun getImageData(key:String){
        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key +".jpeg")

// ImageView in your Activity
        val imageViewFromFB = binding.cContentRimageArea

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                Log.d("itm", "2023")
            }
        }
    }

    private fun getBoardData(key: String){

        //파이어 베이스에서 데이터를 이제 가져오는 부분이 바로 아래 부분임
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dataModel = dataSnapshot.getValue(BoardModel::class.java)



                binding.cTitleAreaRname.setText(dataModel?.title_Rname)
                binding.cContentRingredient.setText(dataModel?.content_Ring)
                binding.cContentRcontent.setText(dataModel?.content_Rcon)
                writerUid = dataModel!!.uid


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }







}