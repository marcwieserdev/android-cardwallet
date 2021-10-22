package fr.marcwieser.qrwallet.tuto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import fr.marcwieser.qrwallet.R
import kotlinx.android.synthetic.main.fragment_tuto.view.*

class TutoFragment : Fragment() {

    private val onPermissionRequested = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) onScanClicked(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tuto, container, false)

        view.btn_scan.setOnClickListener(::onScanClicked)

        return view
    }

    private fun onScanClicked(view: View?) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askCameraPermission()
            return
        }
        findNavController().navigate(R.id.scannerFragment)
    }

    private fun askCameraPermission() = onPermissionRequested.launch(Manifest.permission.CAMERA)
}