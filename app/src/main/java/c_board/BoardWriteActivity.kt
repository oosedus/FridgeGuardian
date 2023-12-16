package c_board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.ActivityBoardWriteBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import utils.FBAuth
import utils.FBRef
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBoardWriteBinding


    //사용자가 이미지를 업로드 하지 않으면, 이미지 뷰 영역이 시꺼매지는 경우가 있음
    //이를 방지하고자, 이미지 업로드 버튼을 누르면 이미지 업로드 로직이 돌아가고, 이미지 업로드 버튼 자체를 누르지 않으면 이미지 업로드 로직 자체가 돌아가지 않게 해보자.
    private var isImageUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_board_write)

        binding.cWriteBtnUpload.setOnClickListener{

            val title_Rname = binding.cTitleAreaRname.text.toString()
            val content_Ring = binding.cContentRingredient.text.toString()
            val content_Rcon = binding.cContentRcontent.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()


            /*
            파이어베이스 store에 이미지를 저장하고 싶다
            근데 이미지의 이름을 ("moutain".jpg) 게시글의 key값으로 설정하고 싶다.

            만약에 내가 게시글을 클릭했을 때, 게시글에 대한 정보를 받아와야 하는데
            이미지 이름에 대한 정보를 모르기 때문에, 이미지 이름을 문서의 key값으로 해줘서 이미지에 대한 정보를 찾기 쉽게 해놓음
            하나의 게시글에 대한 key값을 가져오자

            (이 부분이 밑에 있는 a,b)부분임
             */
           val key = FBRef.boardRef.push().key.toString()

            FBRef.boardRef
                .child(key)
                .setValue(BoardModel(title_Rname , content_Ring , content_Rcon ,uid ,time ))


            // 파이어베이스 구조를 어떻게 해서 게시판 데이터를 넣을 것인가
            /*
             board
                - key (이건 그냥 고유한 값임)
                    -boardModel(title_Rname, content_Ring, content_Rcon, uid, time (글을 쓴 날자))
             */

//            FBRef.boardRef
//                .push()
//                .setValue(BoardModel(title_Rname,content_Ring,content_Rcon,uid,time))

            Toast.makeText(this,"Success to Upload",Toast.LENGTH_LONG).show()

            if(isImageUpload==true) {
                //a
                imageUpload(key)
            }
            finish()

        }

        binding.cContentRimageArea.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,100)
            isImageUpload=true
        }

    }

    private fun imageUpload( key : String ){
        // Get the data from an ImageView as bytes
        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        //b
        val mountainsRef = storageRef.child(key + ".jpeg")

        val imageView = binding.cContentRimageArea

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==RESULT_OK&&requestCode==100){
            binding.cContentRimageArea.setImageURI(data?.data)
        }
    }


}