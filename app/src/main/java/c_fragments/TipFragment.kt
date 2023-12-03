package c_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import c_contentsList.ContentListActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.FragmentTipBinding


class TipFragment : Fragment() {

    private lateinit var binding : FragmentTipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tip,container, false)

        binding.cTipAll.setOnClickListener{

            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category1")
            startActivity(intent)

        }

        binding.cTipKf.setOnClickListener{

            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category2")
            startActivity(intent)

        }

        binding.cTipCf.setOnClickListener{

            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category3")
            startActivity(intent)

        }

        binding.cTipJf.setOnClickListener{

            val intent = Intent(context,ContentListActivity::class.java)
            intent.putExtra("category","category4")
            startActivity(intent)

        }



        binding.cHomeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_tipFragment_to_homeFragment)
        }

        binding.cTalkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_tipFragment_to_talkFragment)
        }

        binding.cBookmarkTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_tipFragment_to_bookmarkFragment)
        }


        return binding.root
    }


}