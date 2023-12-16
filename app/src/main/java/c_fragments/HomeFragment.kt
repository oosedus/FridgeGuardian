package c_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.fridgeguardian.R
import com.example.fridgeguardian.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

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
        return binding.root
    }


}