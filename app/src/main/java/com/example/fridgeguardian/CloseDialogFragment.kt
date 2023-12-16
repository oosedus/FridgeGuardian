// CloseDialogFragment.kt
package com.example.fridgeguardian

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CloseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.c_main_x_dialog, null)

            // YES button
            val yesButton: Button = view.findViewById(R.id.yesButton)
            yesButton.setOnClickListener {
                val intent = Intent (it.context, HomeActivity::class.java)
                it.context.startActivity(intent)
            }

            // NO button
            val noButton: Button = view.findViewById(R.id.noButton)
            noButton.setOnClickListener {
                dismiss() // Dismiss the dialog
            }

            builder.setTitle("Are you sure you want to leave the community?")
                .setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
