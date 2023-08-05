package com.slonoparti.texteditor.fileinteractions.models

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.slonoparti.texteditor.Task
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class SaveFileTask(override var file: File): Task() {
    fun executeTask(data:SpannableString){
        val writer = BufferedWriter(FileWriter(file))
        writer.write(data.toString())
        writer.close()
    }

    override fun requirePermissions(context: Activity){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            val intent = Intent()
            intent.action= Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            ContextCompat.startActivity(context, intent, Bundle())
        }
        else{
            ActivityCompat.requestPermissions(context,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
    }
}