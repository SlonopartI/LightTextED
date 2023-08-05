package com.slonoparti.texteditor.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import com.slonoparti.texteditor.R
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.util.stream.Collectors

class SettingsActivity : AppCompatActivity(){
    companion object{
        var textSize:Int=44
        var isNumerationEnabled:Boolean=false
        private val originalTypefaces:Array<Int> = arrayOf(
            Typeface.NORMAL,Typeface.ITALIC,Typeface.BOLD,Typeface.BOLD_ITALIC)
        fun applySettingsToText(text:Editable,context:Context){
            val settings=BufferedReader(FileReader(context.openFileInput("Settings.txt").fd))
            var temp=""
            for(str:String in settings.lines().collect(Collectors.toList())){
                temp=temp+str+"\n"
            }
            val tempList = temp.replace("\n"," ").split(" ")
            val typeface= originalTypefaces[tempList[0].toInt()]
            textSize=tempList[1].toInt()
            text.setSpan(StyleSpan(typeface),0,text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(AbsoluteSizeSpan(textSize),0,text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            settings.close()
        }
    }
    var textSize:Int=44
    var selectedFont:Int=0
    private val typefaces:Array<String> = arrayOf("Default","Italic","Bold","Bold Italic")
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val switch:Switch=findViewById(R.id.switch1)
        switch.isChecked= isNumerationEnabled
        switch.setOnClickListener {
            isNumerationEnabled = !isNumerationEnabled
        }
        val spinner:Spinner=findViewById(R.id.spinner)
        val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,typefaces)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=arrayAdapter
        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedFont=position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val textSizeView:EditText=findViewById(R.id.editTextNumber)
        val settingsFile=BufferedReader(FileReader(openFileInput("Settings.txt").fd))
        var temp=""
        for(str:String in settingsFile.lines().collect(Collectors.toList())){
            temp=temp+str+"\n"
        }
        val tempList = temp.replace("\n"," ").split(" ")
        textSize=tempList[1].toInt()
        selectedFont=tempList[0].toInt()
        textSizeView.setText(textSize.toString())
        spinner.setSelection(selectedFont)
        settingsFile.close()
        findViewById<Button>(R.id.saveSettingsButton).setOnClickListener {
            textSize=textSizeView.text.toSpannable().toString().toInt()
            val settings=BufferedWriter(FileWriter(openFileOutput("Settings.txt", MODE_PRIVATE).fd))
            settings.write(selectedFont.toString()+"\n")
            settings.write(textSize.toString()+"\n")
            settings.close()
        }
    }
}