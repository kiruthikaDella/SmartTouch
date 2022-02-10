package com.voinismartiot.voni.common.utils

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.voinismartiot.voni.AppDelegate
import java.io.File
import java.io.IOException

object FileHelper {

    /**
     * Get File from intent
     */
    fun getFile(data: Intent?): File? {
        val path = getFilePath(data)
        if (path != null) {
            return File(path)
        }
        return null
    }

    /**
     * Get File Path from intent
     */
    fun getFilePath(data: Intent?): String? {
        return data?.getStringExtra(Constants.EXTRA_FILE_PATH)
    }

    fun getRealPathFromUri(contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = AppDelegate.instance.contentResolver.query(contentUri, proj, null, null, null)
            cursor?.let {
                val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                it.moveToFirst()
                it.getString(columnIndex)
            }

        } finally {
            cursor?.close()
        }
    }

    fun getImageOrientation(imagePath: String?): Int {
        var rotate = 0
        try {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imagePath!!)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            val orientation = exif!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            rotate =
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_270 -> 90
                    else -> 0
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    val File.size get() = if (!exists()) 0.0 else length().toDouble()
    val File.sizeInKb get() = size / 1024
    val File.sizeInMb get() = sizeInKb / 1024
}