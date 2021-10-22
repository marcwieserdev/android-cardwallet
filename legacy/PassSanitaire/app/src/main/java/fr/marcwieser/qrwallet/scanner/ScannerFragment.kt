package fr.marcwieser.qrwallet.scanner

import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import fr.marcwieser.qrwallet.MainActivity
import fr.marcwieser.qrwallet.R
import kotlinx.android.synthetic.main.fragment_scanner.*
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
        .build()

    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraSelector : CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()


    @androidx.camera.core.ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun imageAnalysis(imageProxy: ImageProxy) = (requireActivity() as MainActivity).model.analyzeImage(imageProxy) {
        (requireActivity() as MainActivity).model.insertPass(it)
        findNavController().navigate(R.id.passFragment)
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun bindPreview() {
        val preview : Preview = Preview.Builder()
            .build()
        preview.setSurfaceProvider(executor, previewView.surfaceProvider)
        cameraProvider?.unbindAll()
        imageAnalysis.setAnalyzer(executor, ::imageAnalysis)
        cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }
}