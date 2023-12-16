package c_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import c_board.BoardInsdieActivity
import c_board.BoardListLVAdapter
import c_board.BoardModel
import c_board.BoardWriteActivity
import c_contentsList.BookmarkRVAdapter
import c_contentsList.ContentModel
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.FragmentTalkBinding
import com.example.fridgeguardian.databinding.FragmentTipBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import utils.FBRef


class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding

    //BoardModel형태로 데이터를 받아올건데 받아올 곳이 필요해서 만든,,받아올 곳
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()

    private val TAG = TalkFragment::class.java.simpleName

    private lateinit var boardRVAdapter: BoardListLVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_talk,container, false)

//        val boardList = mutableListOf<BoardModel>()
//        boardList.add(BoardModel("a","b","c","d","e"))

        boardRVAdapter = BoardListLVAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        /*
        TalkFragment에서 레시피리스트들을 보고, 클릭 후 자세한 레시피 내용을 보고 싶을 때,
        기술적 루틴을 다각화하기 위해서 2가지 방법을 사용했습니다
        1. listView에 있는 데이터, 레시피이름, 레시피 재료, 레시피 내용을 다 다른 액티비티로 전달해줘서 만들기
        2. Firebase에 있는 board에 대한 데이터의 id를 기반으로 다시 데이터를 받아오는 방법
         */

        binding.boardListView.setOnItemClickListener{parent,view, position, id ->

//            val intent = Intent(context, BoardInsdieActivity::class.java)
//            intent.putExtra("Recipe name",boardDataList[postion].title_Rname)
//            intent.putExtra("Recipe Ingredient",boardDataList[postion].content_Ring)
//            intent.putExtra("Recipe content",boardDataList[postion].content_Rcon)
//            intent.putExtra("time",boardDataList[postion].time)

            val intent = Intent(context,BoardInsdieActivity::class.java)
            intent.putExtra("key",boardKeyList[position])
            startActivity(intent)



        }



        binding.cWriteBtn.setOnClickListener{
            val intent = Intent(context,BoardWriteActivity::class.java)
            startActivity(intent)
        }

        binding.cHomeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment)
        }

        binding.cTipTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_tipFragment)
        }

        binding.cBookmarkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)
        }

        getFBBoardData()


        return binding.root
    }

    //파이어베이스에 등록된 레시피 정보 가져오기
    private fun getFBBoardData(){
        //파이어 베이스에서 데이터를 이제 가져오는 부분이 바로 아래 부분임
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear()

                //원래는 데이터 스냅샷이 한 번에 뽑히는데, for문 통해서 분리해서 뽑는 부분
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG,dataModel.toString())
                   //dataModel.key

                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())
                }
                boardKeyList.reverse()
                boardDataList.reverse() // 게시글을 최신순이 맨 위에 오게끔 정렬하는거
                boardRVAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)

    }

}