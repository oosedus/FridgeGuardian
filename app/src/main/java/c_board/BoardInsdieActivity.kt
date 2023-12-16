package c_board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityBoardInsdieBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import utils.FBRef


class BoardInsdieActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBoardInsdieBinding

    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_insdie)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_insdie)

        //첫 번째 방법
//        val bi_rname = intent.getStringExtra("Recipe name").toString()
//        val bi_ring = intent.getStringExtra("Recipe Ingredient").toString()
//        val bi_rcon = intent.getStringExtra("Recipe name").toString()
//        val bi_time = intent.getStringExtra("time").toString()
//
//        binding.biContentArea.text = bi_rcon
//        binding.biIngredientArea.text = bi_ring
//        binding.biTimeArea.text = bi_time
//        binding.biTitleArea.text = bi_rname

        binding.boardSettingIcon.setOnClickListener{

            showDialog()
        }


        //두 번째 방법
        key = intent.getStringExtra("key").toString()
        //여기서 key값이 게시글하나에 대한 키값임. a라는 사람이 chicken이라는 이름으로 게시글을 만들었으면 그 레시피 전체 하나에 대한 key값
        getBoardData(key)
        getImageData(key)

    }

    // 게시글에서 삼지창 버튼 누르면 삭제하고, 편집하는 부분을 다이얼로그를 띄워서 구현함
    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.c_custom_dialog,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Post Edit/Delete")

       val alertDialog = mBuilder.show()
       alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(this,"Clicked the edit button",Toast.LENGTH_LONG).show()

           val intent = Intent(this,BoardEditActivity::class.java)
           intent.putExtra("key",key)
           startActivity(intent)


       }

        alertDialog.findViewById<Button>(R.id.deleteBtn)?.setOnClickListener{
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this,"Success to Delete",Toast.LENGTH_LONG).show()
           finish()
            //파이어베이스에 board에 대한 정보를 가져오는데, 보드 전체를 다 지울 수 없으니, child의 key값을 받아와서 걍 지우면 된다
        }




    }

    private fun getImageData(key:String){
        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key +".jpeg")

// ImageView in your Activity
        val imageViewFromFB = binding.getImageArea

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

   //겟보드 데이터가 파이어베이스에 데이터가 변동되면 자동으로 가져옴 근데 이 부분이 삭제할 때 문제임,,
    private fun getBoardData(key: String){

        //파이어 베이스에서 데이터를 이제 가져오는 부분이 바로 아래 부분임
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)

                    binding.biContentArea.text = dataModel?.content_Rcon
                    binding.biIngredientArea.text = dataModel?.content_Ring
                    binding.biTitleArea.text = dataModel?.title_Rname
                    binding.biTimeArea.text = dataModel?.time
                }catch (e:Exception) {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }

}