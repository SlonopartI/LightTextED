package com.slonoparti.texteditor.fileinteractions.tasks

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import kotlin.String
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.slonoparti.texteditor.Task
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class OpenFileTask(override var file: File) : Task() {
    fun executeTask(): String{
        val string = StringBuffer()
        val reader = BufferedReader(FileReader(file))
        if(reader.ready()){
            for(str:String in reader.lineSequence()){
                string.append(str+"\n")
            }
        }
        reader.close()
        return string.toString()
    }

    override fun requirePermissions(context: Activity){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            startActivity(context, intent, Bundle())
        }
        else{
            ActivityCompat.requestPermissions(context,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
    }
}