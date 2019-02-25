package com.example.instagr.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity) {
    var imageUri: Uri? = null
    val REQUEST_CODE = 1
    private var simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null) {
            var imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                activity,
                "com.example.instagr.fileprovider",
                imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val storageDir: File = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

    }
}