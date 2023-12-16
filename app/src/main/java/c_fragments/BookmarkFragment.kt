package c_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import c_contentsList.BookmarkRVAdapter
import c_contentsList.ContentModel
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.FragmentBookmarkBinding
import com.example.fridgeguardian.databinding.FragmentTalkBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import utils.FBAuth
import utils.FBRef


class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding

    val bookmarkIdList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()


    lateinit var rvAdapter:BookmarkRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bookmark,container, false)


        //2. 사용자가 북마크한 정보를 다 가져옴
        getBookmarkData()

        rvAdapter = BookmarkRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val rv : RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(),2)




        binding.cHomeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        binding.cTipTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }

        binding.cTalkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }


        return binding.root

    }

    private fun getCategoryData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //원래는 데이터 스냅샷이 한 번에 뽑히는데, for문 통해서 분리해서 뽑는 부분
                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(ContentModel::class.java)
                    //3. 전체 컨텐츠 중에서, 사용자가 북마크한 정보만 보여줌!
                    if(bookmarkIdList.contains(dataModel.key.toString())){
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }


                }
                rvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.category1 .addValueEventListener(postListener)
        FBRef.category2 .addValueEventListener(postListener)



    }

    private fun getBookmarkData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //원래는 데이터 스냅샷이 한 번에 뽑히는데, for문 통해서 분리해서 뽑는 부분
                for (dataModel in dataSnapshot.children) {

                    bookmarkIdList.add(dataModel.key.toString())

                }

                //1. 전체 카테고리에 있는 컨텐츠 데이터들을 다 가져옴!
                getCategoryData()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)


    }


}