package utils

//파이어베이스에서 uid값 가져오는 부분
//"FBAuth.getUid()"하면 됩니다
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FBAuth {

    companion object{

        private lateinit var auth:FirebaseAuth

        fun getUid():String{
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()

        }
        //커뮤니티 게시판에서 작성게시글의 작성시간 가져오려고
        fun getTime():String{
            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

            return dateFormat
        }
    }



}