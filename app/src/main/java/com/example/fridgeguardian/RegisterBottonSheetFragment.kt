package com.example.fridgeguardian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RegisterBottomSheetFragment : BottomSheetDialogFragment() {

    interface RegisterOptionClickListener {
        fun onKeyboardRegistrationClick()
        fun onVoiceRegistrationClick()
    }

    var listener: RegisterOptionClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_register_option, container, false)

        view.findViewById<LinearLayout>(R.id.keyboard_registration_layout).setOnClickListener {
            listener?.onKeyboardRegistrationClick()
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.voice_registration_layout).setOnClickListener {
            listener?.onVoiceRegistrationClick()
            dismiss()
        }

        return view
    }
}