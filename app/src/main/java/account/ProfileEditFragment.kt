package account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fridgeguardian.databinding.FragmentProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userEmail = arguments?.getString("user_email") ?: FirebaseAuth.getInstance().currentUser?.email
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val userInfo = userEmail?.let { db.userDao().getUserByEmail(it) }
            if (userInfo != null) {
                withContext(Dispatchers.Main) {
                    // Update UI with RoomDB data on the main thread
                    binding.editTextName.setText(userInfo.name)
                    binding.editTextNickname.setText(userInfo.nickname)
                    binding.editTextPhoneNumber.setText(userInfo.phoneNumber)
                }
            }
        }

        binding.btnSaveChanges.setOnClickListener {
            if (validateInputs()) {
                saveUserChanges()
                // Navigate back to MyPageActivity
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateInputs(): Boolean {
        // Validate each input field and show error if empty
        var isValid = true

        listOf(binding.editTextName, binding.editTextNickname, binding.editTextPhoneNumber).forEach { editText ->
            if (editText.text.toString().trim().isEmpty()) {
                editText.error = "This field cannot be empty"
                isValid = false
            } else {
                editText.error = null // Clear error
            }
        }
        return isValid
    }

    private fun saveUserChanges() {
        userEmail?.let { email ->
            val updatedUser = User(
                email = email,
                name = binding.editTextName.text.toString().trim(),
                nickname = binding.editTextNickname.text.toString().trim(),
                phoneNumber = binding.editTextPhoneNumber.text.toString().trim()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                db.userDao().updateUser(updatedUser)
            }
        }
    }
}