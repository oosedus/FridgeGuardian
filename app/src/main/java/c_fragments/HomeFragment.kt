package c_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import c_contentsList.BookmarkRVAdapter
import c_contentsList.ContentModel
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import utils.FBRef

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding



    val bookmarkIdList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()

    lateinit var rvAdapter: BookmarkRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.cHomeTap.setOnClickListener {


            Toast.makeText(context,"Clicked home tap",Toast.LENGTH_LONG).show()
        }

        binding.cBookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)

            Toast.makeText(context,"Clicked Bookmark tap",Toast.LENGTH_LONG).show()
        }

        binding.cTipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_tipFragment)

            Toast.makeText(context,"Clicked tip tap",Toast.LENGTH_LONG).show()
        }

        binding.cTalkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_talkFragment)

            Toast.makeText(context,"Clicked talk tap",Toast.LENGTH_LONG).show()
        }
        rvAdapter = BookmarkRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val rv : RecyclerView = binding.mainRV
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(),2)


        getCategoryData()

        return binding.root
    }

    private fun getCategoryData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //원래는 데이터 스냅샷이 한 번에 뽑히는데, for문 통해서 분리해서 뽑는 부분
                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(ContentModel::class.java)

                    items.add(item!!)
                    itemKeyList.add(dataModel.key.toString())

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


}