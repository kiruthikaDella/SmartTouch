package com.dellainfotech.smartTouch.common.utils

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.dellainfotech.smartTouch.AppDelegate
import java.io.File


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
}