package utils

//파이어베이스의 Ref값을 넣어주는 부분임
//" FBRef.bookmarkRef.child(FBAuth.getUid()).child(key).setValue("Good")
//bookmarkRef의 값을 가져와서 child가지에 FBAuth의 uid값을 넣는다. 그리고 uid안에 key값(contents id값)을 넣는다. " 이렇게 사용할 수 있음
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object {
        private val database = Firebase.database

        val category1 = database.getReference("contents")
        val category2 = database.getReference("contents2")

        val bookmarkRef = database.getReference("bookmark_list")

        //커뮤니티 게시글을 board에 저장할거임
        val boardRef = database.getReference("board")
    }
}