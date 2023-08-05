package com.slonoparti.texteditor.fileinteractions

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.mikepenz.iconics.Iconics.applicationContext
import com.slonoparti.texteditor.MainActivity
import com.slonoparti.texteditor.R
import com.slonoparti.texteditor.fileinteractions.models.OpenFileTask
import ru.bartwell.exfilepicker.ExFilePicker
import java.io.File

class FileFragment : DialogFragment(){
    private lateinit var editText:EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog:AlertDialog.Builder=AlertDialog.Builder(activity)
        val view=activity?.layoutInflater?.inflate(R.layout.filefragment,null)
        editText= view?.findViewById(R.id.editTextText)!!
        return dialog.setView(view)
            .setPositiveButton("Сохранить"
            ) { _, _ -> addFile() }
            .setNegativeButton("Отмена",null)
            .create()
    }

    private fun addFile() {
        val file = File(editText.text.toString())
        val activity: FragmentActivity? = this.activity
        if(activity is MainActivity){
            activity.notCurrentlyCreatedFile=file
            if(PackageManager.PERMISSION_GRANTED==applicationContext.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                val picker = ExFilePicker()
                picker.setCanChooseOnlyOneItem(true)
                picker.setSortButtonDisabled(true)
                picker.setNewFolderButtonDisabled(true)
                picker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES)
                picker.start(activity,1)
            }
            else{
                OpenFileTask(file).requirePermissions(activity)
            }
        }
    }
}