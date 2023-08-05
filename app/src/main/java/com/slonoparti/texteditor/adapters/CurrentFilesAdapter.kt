package com.slonoparti.texteditor.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.slonoparti.texteditor.MainActivity
import com.slonoparti.texteditor.R
import com.slonoparti.texteditor.fileinteractions.tasks.OpenFileTask
import java.io.File

class CurrentFilesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var fileNames:Array<String>
    lateinit var filePaths:Array<String>
    lateinit var activity:MainActivity

    class FileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView:TextView=itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fileitem,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(fileNames.isNotEmpty()){
            val textView:TextView=holder.itemView.findViewById(R.id.textView)
            textView.text=fileNames[position]
            val drawable = GradientDrawable()
            drawable.setStroke(1, Color.GRAY)
            textView.background=drawable
        }
        holder.itemView.setOnClickListener {
            if(!File(filePaths[position]).exists()||!File(filePaths[position]).canRead()){
                Toast.makeText(activity,"Файл недоступен или мог быть удален",Toast.LENGTH_SHORT).show()
                val editText: EditText = activity.findViewById(R.id.customEditText)
                editText.setText("")
                activity.currentFileIndex = position
            }
            else{
                val model = OpenFileTask(File(filePaths[position]))
                val str=model.executeTask()
                val text = SpannableString(str)
                val editText: EditText = activity.findViewById(R.id.customEditText)
                editText.setText(text)
                activity.currentFileIndex = position
            }
        }
    }

    override fun getItemCount(): Int {
        return fileNames.size
    }
}