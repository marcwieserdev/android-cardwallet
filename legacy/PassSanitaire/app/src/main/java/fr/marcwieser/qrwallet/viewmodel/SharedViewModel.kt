package fr.marcwieser.qrwallet.viewmodel

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import fr.marcwieser.qrwallet.db.AppDatabase
import fr.marcwieser.qrwallet.db.SanitaryPass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable


class SharedViewModel : ViewModel() {

    companion object {
        private const val DB_NAME = "pass-db"
    }

    private lateinit var db: AppDatabase
    private val barcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    fun init(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }

    fun getAllPass(block: (List<SanitaryPass>) -> Unit) = viewModelScope.launch {
        val passes = withContext(Dispatchers.IO) { db.sanitaryDao().getAll() }
        block(passes)
    }

    @androidx.camera.core.ExperimentalGetImage
    fun analyzeImage(imageProxy: ImageProxy, onSuccess: (code: String) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            BarcodeScanning.getClient(barcodeScannerOptions).process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { onSuccess(it) }
                }.addOnCompleteListener { imageProxy.close() }
        } else imageProxy.close()
    }

    fun insertPass(code: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            db.sanitaryDao().insertAll(SanitaryPass(0, code))
        }
    }

    fun updatePass(pass: SanitaryPass) = viewModelScope.launch {
        withContext(Dispatchers.IO) { db.sanitaryDao().update(pass) }
    }

    fun generateQrCode(code: String): Bitmap {
        val matrix = QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, 900, 900)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = (if (matrix[x, y]) 0xFF000000 else 0xFFFFFFFF).toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}