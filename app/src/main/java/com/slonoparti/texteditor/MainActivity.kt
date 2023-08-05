package com.slonoparti.texteditor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.slonoparti.texteditor.activities.SettingsActivity
import com.slonoparti.texteditor.adapters.CurrentFilesAdapter
import com.slonoparti.texteditor.fileinteractions.FileFragment
import com.slonoparti.texteditor.fileinteractions.models.OpenFileTask
import com.slonoparti.texteditor.fileinteractions.models.SaveFileTask
import com.slonoparti.texteditor.textclasses.CustomEditText
import com.slonoparti.texteditor.textclasses.CustomTextWatcher
import ru.bartwell.exfilepicker.ExFilePicker
import ru.bartwell.exfilepicker.data.ExFilePickerResult
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mHeader: AccountHeader
    private lateinit var mDrawer: Drawer
    private lateinit var fileTask: Task
    private lateinit var currentFilesPaths:ArrayList<String>
    var currentFileIndex:Int=0
    var textWatcher: CustomTextWatcher= CustomTextWatcher(this)
    lateinit var notCurrentlyCreatedFile:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var tempFile=File(this.filesDir.path,"Settings.txt")
        if(!tempFile.exists()){
            val settingsWriter=openFileOutput("Settings.txt", MODE_PRIVATE)
            settingsWriter.write("0\n44\n".toByteArray())
            settingsWriter.close()
        }
        findViewById<CustomEditText>(R.id.customEditText).addTextChangedListener(textWatcher)
        tempFile=File(this.filesDir.path,"CurrentFiles.txt")
        currentFilesPaths= ArrayList()
        if(!tempFile.exists()){
            val currentFiles=openFileOutput("CurrentFiles.txt", MODE_PRIVATE)
            currentFiles.write("0\n".toByteArray())
            currentFiles.close()
            tempFile=File.createTempFile("Temp",".txt")
            currentFiles.close()
        }
        else{
            val currentFiles=BufferedReader(FileReader(openFileInput("CurrentFiles.txt").fd))
            var i=0
            val iterator=currentFiles.lines().iterator()
            while(iterator.hasNext()){
                if(i==0){
                    currentFileIndex=iterator.next().toInt()
                    i++
                }
                else{
                    currentFilesPaths.add(iterator.next())
                }
            }
            tempFile=if(currentFilesPaths.isNotEmpty()) File(currentFilesPaths[currentFileIndex])
                    else File.createTempFile("Temp",".txt")
            findViewById<CustomEditText>(R.id.customEditText).setText(tempFile.readText())
            currentFiles.close()
        }
        val recyclerView:RecyclerView=findViewById(R.id.recycler_view)
        val adapter= CurrentFilesAdapter()
        adapter.fileNames= getFilesNames() as Array<String>
        adapter.filePaths= currentFilesPaths.toTypedArray()
        adapter.activity=this
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        fileTask= OpenFileTask(tempFile)
    }

    override fun onStart() {
        super.onStart()
        initFunc()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("currentFilesPaths",currentFilesPaths)
        outState.putInt("currentFileIndex",currentFileIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentFilesPaths= savedInstanceState.getStringArrayList("currentFilesPaths") as ArrayList<String>
        currentFileIndex=savedInstanceState.getInt("currentFileIndex")
        fileTask.file=if(currentFilesPaths.isNotEmpty()) File(currentFilesPaths[currentFileIndex])
                else File.createTempFile("Temp",".txt")
        textWatcher.activity=this
    }

    private fun initFunc(){
        setSupportActionBar(findViewById(R.id.toolbar))
        createHeader()
        createDrawer(this)
    }

    private fun createDrawer(context:Activity){
        mDrawer=DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(mHeader)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withToolbar(findViewById(R.id.toolbar))
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(1)
                    .withIconTintingEnabled(true)
                    .withName("Новый файл")
                    .withSelectable(false)
                    .withIcon(android.R.drawable.ic_menu_add)
                    .withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener  {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            fileTask= OpenFileTask(getFile())
                            val fragment=FileFragment()
                            fragment.show(supportFragmentManager,"FileName")
                            return true
                        }

                    }),
                PrimaryDrawerItem().withIdentifier(2)
                    .withIconTintingEnabled(true)
                    .withName("Открыть файл")
                    .withSelectable(false)
                    .withIcon(android.R.drawable.ic_menu_more)
                    .withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener  {
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            fileTask= OpenFileTask(getFile())
                            if(PackageManager.PERMISSION_GRANTED==applicationContext.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                                val picker = ExFilePicker()
                                picker.setCanChooseOnlyOneItem(true)
                                picker.setSortButtonDisabled(true)
                                picker.setNewFolderButtonDisabled(true)
                                picker.setChoiceType(ExFilePicker.ChoiceType.FILES)
                                picker.start(context,0)
                            }
                            else{
                                (fileTask as OpenFileTask).requirePermissions(context)
                            }
                            return true
                        }

                    }),
                PrimaryDrawerItem().withIdentifier(3)
                    .withIconTintingEnabled(true)
                    .withName("Сохранить файл")
                    .withSelectable(false)
                    .withIcon(android.R.drawable.ic_menu_save)
                    .withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener{
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            fileTask= SaveFileTask(getFile())
                            if(PackageManager.PERMISSION_GRANTED==applicationContext.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                (fileTask as SaveFileTask).executeTask(findViewById<EditText>(R.id.customEditText).text.toSpannable() as SpannableString)
                                Toast.makeText(context,"Файл сохранён",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                (fileTask as SaveFileTask).requirePermissions(context)
                            }
                            return true
                        }
                    }),
                PrimaryDrawerItem().withIdentifier(4)
                    .withIconTintingEnabled(true)
                    .withName("Закрыть файл")
                    .withSelectable(false)
                    .withIcon(android.R.drawable.ic_delete)
                    .withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener{
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            deleteCurrentFile()
                            return true
                        }
                    }),
                PrimaryDrawerItem().withIdentifier(5)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener{
                        override fun onItemClick(
                            view: View?,
                            position: Int,
                            drawerItem: IDrawerItem<*>
                        ): Boolean {
                            val intent=Intent(context,SettingsActivity::class.java)
                            startActivity(intent)
                            return true
                        }

                    })
            ).build()
    }

    private fun createHeader(){
        mHeader= AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.drawable.header)
            .build()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0||requestCode==1){
            val result = ExFilePickerResult.getFromIntent(data)
            if (result != null && result.count > 0) {
                if(PackageManager.PERMISSION_GRANTED==this.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)&&Build.VERSION.SDK_INT<Build.VERSION_CODES.R){
                    val file:File = if(requestCode==1){
                        notCurrentlyCreatedFile=File(result.path+result.names[0]+"/",notCurrentlyCreatedFile.name)
                        notCurrentlyCreatedFile.createNewFile()
                        notCurrentlyCreatedFile
                    } else{
                        File(result.path+ result.names[0])
                    }
                    currentFilesPaths.add(file.path)
                    currentFileIndex=currentFilesPaths.size-1
                    fileTask.file=File(currentFilesPaths[currentFileIndex])
                    setValuesForRecyclerView()
                    val str = SpannableString((fileTask as OpenFileTask).executeTask())
                    val text: CustomEditText =findViewById(R.id.customEditText)
                    text.setText(str)
                    saveCurrentFiles()
                }
                else if(PackageManager.PERMISSION_GRANTED==this.checkCallingOrSelfPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)&&Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                    val file:File = if(requestCode==1){
                        notCurrentlyCreatedFile=File(result.path+result.names[0]+"/",notCurrentlyCreatedFile.name)
                        notCurrentlyCreatedFile.createNewFile()
                        notCurrentlyCreatedFile
                    } else{
                        File(result.path+ result.names[0])
                    }
                    currentFilesPaths.add(file.path)
                    currentFileIndex=currentFilesPaths.size-1
                    fileTask.file=File(currentFilesPaths[currentFileIndex])
                    setValuesForRecyclerView()
                    val str = SpannableString((fileTask as OpenFileTask).executeTask())
                    val text: CustomEditText =findViewById(R.id.customEditText)
                    text.setText(str)
                    saveCurrentFiles()
                }
                else{
                    (fileTask as OpenFileTask).requirePermissions(this)
                }
            }
        }
    }

    private fun saveCurrentFiles() {
        val currentFiles=openFileOutput("CurrentFiles.txt", MODE_PRIVATE)
        currentFiles.write((currentFileIndex.toString()+"\n").toByteArray())
        for(path:String? in currentFilesPaths){
            currentFiles.write((path+"\n").toByteArray())
        }
        currentFiles.close()
    }

    private fun setValuesForRecyclerView(){
        val recyclerView:RecyclerView=findViewById(R.id.recycler_view)
        val adapter= CurrentFilesAdapter()
        adapter.fileNames= getFilesNames() as Array<String>
        val tempArray:Array<String?> = arrayOfNulls(currentFilesPaths.size)
        System.arraycopy(currentFilesPaths.toArray(),0,tempArray,0,currentFilesPaths.size)
        adapter.filePaths=tempArray as Array<String>
        adapter.activity=this
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
    }

    private fun deleteCurrentFile(){
        currentFilesPaths.removeAt(currentFileIndex)
        if(currentFilesPaths.isNotEmpty()){
            if(currentFileIndex>0)currentFileIndex--
            fileTask.file=File(currentFilesPaths[currentFileIndex])
            fileTask= OpenFileTask(fileTask.file)
            findViewById<EditText>(R.id.customEditText).setText(SpannableString((fileTask as OpenFileTask).executeTask()))
            setValuesForRecyclerView()
            saveCurrentFiles()
        }
        else {
            currentFileIndex=0
            findViewById<EditText>(R.id.customEditText).setText(SpannableString(""))
            fileTask.file=File.createTempFile("Temp",".txt")
            setValuesForRecyclerView()
            saveCurrentFiles()
        }
    }

    private fun getFilesNames(): Array<String?> {
        val array:Array<String?> = arrayOfNulls(currentFilesPaths.size)
        var i=0
        while (i<currentFilesPaths.size){
            array[i]=File(currentFilesPaths[i]).name
            i++
        }
        return array
    }
    private fun getFile():File{
        return if(currentFilesPaths.isNotEmpty()) File(currentFilesPaths[currentFileIndex])
        else fileTask.file
    }

}