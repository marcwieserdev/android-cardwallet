package fr.marcwieser.qrwallet.pass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import fr.marcwieser.qrwallet.MainActivity
import fr.marcwieser.qrwallet.R
import fr.marcwieser.qrwallet.db.SanitaryPass
import kotlinx.android.synthetic.main.dialog_additional_info.*
import kotlinx.android.synthetic.main.fragment_pass.*
import kotlinx.android.synthetic.main.fragment_pass.view.*

class PassFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pass, container, false)
        val activity = (requireActivity() as MainActivity)
        // Inflate the layout for this fragment
        activity.model.getAllPass { passes ->
            val pass = passes.first()
            val bitmap = activity.model.generateQrCode(pass.code)

            if (!pass.isAdditionalInfoSet()) openAdditionalInfoDialog(pass)
            else updateView(pass)
            view.qrcode.setImageDrawable(bitmap.toDrawable(resources))
        }
        return view
    }

    private fun updateView(pass: SanitaryPass) {
        firstname.text = pass.firstname
        name.text = pass.lastname
        dob.text = pass.dob
    }

    private fun openAdditionalInfoDialog(pass: SanitaryPass) {
        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(R.string.additional_info_title)
            .setPositiveButton(R.string.save) { dialogInterface, _ ->
                if (!isDialogInputsValid(dialogInterface as AlertDialog)) return@setPositiveButton
                val activity = (requireActivity() as MainActivity)
                val firstname = dialogInterface.findViewById<EditText>(R.id.input_firstname)
                val lastname = dialogInterface.findViewById<EditText>(R.id.input_name)
                val dob = dialogInterface.findViewById<EditText>(R.id.input_dob)
                val updatedPass = pass.copy(
                    lastname = lastname?.text.toString(),
                    firstname = firstname?.text.toString(),
                    dob = dob?.text.toString()
                )
                activity.model.updatePass(updatedPass)
                updateView(updatedPass)
                dialogInterface.dismiss()
            }
            .setView(R.layout.dialog_additional_info)
            .create()

        val firstname = dialog.findViewById<EditText>(R.id.input_firstname)
        val lastname = dialog.findViewById<EditText>(R.id.input_name)
        val dob = dialog.findViewById<EditText>(R.id.input_dob)

        firstname?.setText(pass.firstname)
        lastname?.setText(pass.lastname)
        dob?.setText(pass.dob)
        dialog.show()
    }

    private fun isDialogInputsValid(dialog: AlertDialog): Boolean {
        val firstname = dialog.findViewById<EditText>(R.id.input_firstname) ?: return false
        val lastname = dialog.findViewById<EditText>(R.id.input_name) ?: return false
        val dob = dialog.findViewById<EditText>(R.id.input_dob) ?: return false

        return firstname.text.isNotBlank() && lastname.text.isNotBlank() && dob.text.isNotBlank()
    }

}