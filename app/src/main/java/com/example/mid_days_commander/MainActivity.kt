
package com.example.mid_days_commander

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.BufferedReader


import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

class CustomAdapter(context: Context, private val list: List<File>) : ArrayAdapter<File>(context, android.R.layout.simple_list_item_1, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView
        val file = list[position]
        if (file.canRead() && !file.canWrite()) { // Set the text color of folders to red
            textView.setTextColor(Color.RED)
            textView.alpha
        }
        return view
    }
}


class MainActivity : AppCompatActivity() {
    val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 123
    private val startPathL = "/sdcard"
    private var lastChooseL = ""
    private var currPathL = "/sdcard"
    private val startPathR = "/sdcard"
    private var lastChooseR = ""
    private var currPathR = "/sdcard"

    private var actionPathCheckedLeft = ""
    private var actionPathCheckedRight = ""
    private var lastColumnCheck = ""

    private var creatingFilePath = ""
//    private var mListView = findViewById<ListView>(R.id.left)
//    private var rListView = findViewById<ListView>(R.id.right)


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // Check if the permission request was for the SD card
        if (requestCode == 1) {
            // Check if the permission was granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, so you can access the files on the SD card
                val sdCardRoot = File("/sdcard")
                val files = sdCardRoot.listFiles()
            } else {
                // Permission was denied, so you cannot access the files on the SD card
            }
        }
    }

    fun checkPathPermission(path: String) {
        val path = Paths.get(path)

        // Get the set of POSIX file permissions for the file
        val permissions = Files.getPosixFilePermissions(path)

        // Check if the file is readable
        if (permissions.contains(PosixFilePermission.OWNER_READ)) {
            println("The file is readable")
        }

        // Check if the file is writable
        if (permissions.contains(PosixFilePermission.OWNER_WRITE)) {
            println("The file is writable")
        }
    }

    fun showDialogAlert(contentAlert: String, titleDialog: String) {
        val errorNotification = AlertDialog.Builder(this)

        // Set the title and message of the AlertDialog
        errorNotification.setTitle(titleDialog)
        errorNotification.setMessage(contentAlert)

        val alertDialog = errorNotification.create()
        alertDialog.show()
    }

    fun reloadListing(side: String) {
        var mListView = findViewById<ListView>(R.id.left)
        var rListView = findViewById<ListView>(R.id.right)
        if(side == "l") {
            var arrayAdapterCurr = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                convertToTitle(currPathL)
            )
            mListView.adapter = arrayAdapterCurr
        }
        else if(side == "r"){
            var arrayAdapterCurr = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                convertToTitle(currPathR)
            )
            rListView.adapter = arrayAdapterCurr
        }
        else {

            var arrayAdapterCurr = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                convertToTitle(currPathR)
            )
            rListView.adapter = arrayAdapterCurr

            var arrayAdapterCurrR = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                convertToTitle(currPathL)
            )
            mListView.adapter = arrayAdapterCurrR
        }
    }

    fun File.getExtension(): String {
        // Get the file name
        val fileName = this.name

        // Get the index of the last dot in the file name
        val dotIndex = fileName.lastIndexOf(".")

        // Return the file's extension (the part of the file name after the last dot)
        return fileName.substring(dotIndex + 1)
    }

    // Function to createFile
    fun createFile(filename: String) {
        var file = File(currPathL+"/"+filename+".txt")
        if(lastColumnCheck == "l"){
            println(currPathL+"/"+filename+".txt")
            file = File(currPathL+"/"+filename+".txt")
            creatingFilePath = currPathL+"/"+filename+".txt"
        }
        else {
            println(currPathR+"/"+filename+".txt")
            file = File(currPathR+"/"+filename+".txt")
            creatingFilePath = currPathR+"/"+filename+".txt"
        }
        file.writeText("Hello, world!")

        // Create the file
        file.createNewFile()
    }
    // fun to open img
    @SuppressLint("MissingInflatedId")
    fun openImg(namefile: String, side: String){
        setContentView(R.layout.activity_image)

//        val pathToImg = if(side == "l") currPathL else currPathR

        val imageView = findViewById<ImageView>(R.id.imageViewPresent)

        val image = BitmapFactory.decodeFile(namefile)

        imageView.setImageBitmap(image)
        val buttonBack = findViewById<Button>(R.id.goToHome)

        buttonBack.setOnClickListener {
            println("second view")
            setContentView(R.layout.activity_main)
            reloadListing("both")
        }

    }

    // Fun to open files
    fun openFile (namefile: String="", side: String = "l") {

        var pathToOpen = ""
        if(lastColumnCheck == "l"){
            pathToOpen = currPathL+"/"+actionPathCheckedLeft

        }
        else {
            pathToOpen = currPathR+"/"+actionPathCheckedRight

        }
        if(File(pathToOpen).isDirectory){
            showDialogAlert("I can't open folder", "Open")
        }


        val file = File(pathToOpen)
        println(pathToOpen+"/"+namefile)
        val fileExtension = file.getExtension();

        if(fileExtension == "txt"){
            setContentView(R.layout.activity_second)

            val reader = BufferedReader(file.reader())
            val textView = findViewById<TextView>(R.id.showFileTxt)
            var contentToRender = ""

            var line = reader.readLine()
            while (line != null) {
                println("linia wirtualna")
                println(line)
                contentToRender += line

                line = reader.readLine()
            }
            textView.text = contentToRender

            reader.close()
            val buttonBack = findViewById<Button>(R.id.goToHome)

            buttonBack.setOnClickListener {
                println("second view")
                setContentView(R.layout.activity_main)
                reloadListing("both")
                recreate()


            }

        }
        else if (fileExtension == "jpg" || fileExtension == "png") {
            openImg(pathToOpen,"")
        }
        else {
            val errorNotification = AlertDialog.Builder(this)

            // Set the title and message of the AlertDialog
            errorNotification.setTitle("Open file "+ namefile)
            errorNotification.setMessage("Can't open this format")




            val alertDialog = errorNotification.create()
            alertDialog.show()
        }

    }


    // Conver array files directory to Title
    fun convertToTitle(dir: String): ArrayList<String> {
//        val filesArrStrCurrently = arrayListOf("/..")
        val filesArrStrCurrently = ArrayList<String>()
        filesArrStrCurrently.add("/..")
        val files = File(dir).listFiles()
        for(fil in files) {
            if (fil.canRead() && !fil.canWrite()) {
                // The file is only readable
                filesArrStrCurrently.add(fil.name)

            }
            filesArrStrCurrently.add(fil.name)
        }
        return filesArrStrCurrently;
    }

    fun changeCurrentlyDirectoryOnListView(ListViewVar: ListView, next: String, side: String): ArrayAdapter<*> {

        val arrayAdapterCurr: ArrayAdapter<*>

            if(next == "/..") {
                if(side == "l"){
                    currPathL = currPathL.replace("/"+lastChooseL, "")
                    println(currPathL)
                    arrayAdapterCurr = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        convertToTitle(currPathL)
                    )
                }
                else {
                    currPathR = currPathR.replace("/"+lastChooseL, "")

                    arrayAdapterCurr = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        convertToTitle(currPathR)
                    )
                }


            }
            else {

                if(side == "l") {
                    lastChooseL = next
                    currPathL += "/" + next
                    arrayAdapterCurr = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        convertToTitle(currPathL)
                    )
                }
                else {
                    lastChooseR = next
                    currPathR += "/" + next
                    arrayAdapterCurr = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        convertToTitle(currPathR)
                    )
                }
            }
            return arrayAdapterCurr






    }
    fun copyFile() {

        var pathFrom = ""
        var pathDestiny = ""
        if(lastColumnCheck == "l"){
            pathFrom = currPathL+"/"+actionPathCheckedLeft
            pathDestiny = currPathR+"/"+actionPathCheckedLeft
        }
        else {
            pathFrom = currPathR+"/"+actionPathCheckedRight
            pathDestiny = currPathL+"/"+actionPathCheckedRight
        }

        val sourceFile = File(pathFrom)

        val destinationFile = File(pathDestiny)

        if (destinationFile.isFile) {

            showDialogAlert("Can't copy! File already exists","Copy file")

        } else if (destinationFile.isDirectory) {

            println("File exists and is a directory")

            showDialogAlert("Can't copy! Directory already exists","Copy directory")
        } else {
            sourceFile.copyTo(destinationFile, overwrite = true)
            reloadListing("both")
        }
        // Copy the file


    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // INIT APP

        ActivityCompat.requestPermissions(
            this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        );

        //start directory
        val filesArrStr = arrayListOf("/..")
        val filesArrFile = ArrayList<File>()
        val files = File("/sdcard").listFiles()
        for(fil in files) {
            filesArrStr.add(fil.name)
            filesArrFile.add(fil)
        }

        val arrayAdapter: ArrayAdapter<*>


        var mListView = findViewById<ListView>(R.id.left)

        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, filesArrStr)


        mListView.adapter = arrayAdapter
//        mListView.adapter = adapter



        val arrayAdapterRight: ArrayAdapter<*>
        var rListView = findViewById<ListView>(R.id.right)
        arrayAdapterRight = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, filesArrStr)
        rListView.adapter = arrayAdapterRight




        // left

        mListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position

            val item = adapterView.getItemAtPosition(i)
            lastColumnCheck = "l"
            val directionFolder = item.toString()

            if(File(currPathL+"/"+directionFolder).isDirectory) {
                mListView.adapter =
                    changeCurrentlyDirectoryOnListView(mListView, directionFolder, "l")
            }
            else {
                openFile(directionFolder, "l")
            }
            true
        }


        // right

        rListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position
            val item = adapterView.getItemAtPosition(i)
            lastColumnCheck = "r"
            val directionFolder = item.toString()
            if(File(currPathR+"/"+directionFolder).isDirectory) {
                rListView.adapter = changeCurrentlyDirectoryOnListView(rListView, directionFolder, "r")
            }
            else {
                openFile(directionFolder, "r")
            }
            true
        }


        // button create
        val buttonCreate = findViewById<Button>(R.id.createBtn)

        buttonCreate.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)

            // Create the AlertDialog builder
            val builder = AlertDialog.Builder(this)

            // Set the title and message of the AlertDialog
            builder.setTitle("Create File")
            builder.setMessage("Enter your filename:")
            val input = EditText(this)

            builder.setView(input)
            builder.setPositiveButton("OK") { dialog, _ ->

                val filenameFromInput = input.getText().toString()
                createFile(filenameFromInput)
                reloadListing("l")
//                recreate()

                // open in editor
                setContentView(R.layout.activity_edit)
                val editTextarea = findViewById<EditText>(R.id.editTextarea)
                var contentToRender = ""

                editTextarea.setText(contentToRender)

                val buttonBack = findViewById<Button>(R.id.goToHome)

                buttonBack.setOnClickListener {
                    println("second view")
                    setContentView(R.layout.activity_main)
                    reloadListing("both")
                    recreate()


                }
                val buttonSave = findViewById<Button>(R.id.saveBtn)

                buttonSave.setOnClickListener {
                    println("second view")
                    val currContent = editTextarea.getText()
                    val file = File(creatingFilePath)
                    file.writeText(currContent.toString())
                    setContentView(R.layout.activity_main)
                    reloadListing("both")
                    recreate()


                }
            }
            val alertDialog = builder.create()
            alertDialog.show()



        }
        // button create
        val buttonOpen = findViewById<Button>(R.id.openBtn2)

        buttonOpen.setOnClickListener {

            openFile()
        }


        val buttonCopy = findViewById<Button>(R.id.copyBtn)

        buttonCopy.setOnClickListener{
            copyFile()
        }
        val buttonDel = findViewById<Button>(R.id.delBtn2)

        buttonDel.setOnClickListener{
            var pathToDel = ""

            if(lastColumnCheck == "l"){
                pathToDel = currPathL+"/"+actionPathCheckedLeft

            }
            else {
                pathToDel = currPathR+"/"+actionPathCheckedRight

            }
            val directory = File(pathToDel)
            if (directory.deleteRecursively()) {
                showDialogAlert("Deleted successfully", "Delete")
            } else {

                showDialogAlert("Deleted failed", "Delete")
            }
            reloadListing("both")


        }

        val buttonMove = findViewById<Button>(R.id.moveBtn2)

        buttonMove.setOnClickListener{
            copyFile()
            var pathToDel = ""

            if(lastColumnCheck == "l"){
                pathToDel = currPathL+"/"+actionPathCheckedLeft

            }
            else {
                pathToDel = currPathR+"/"+actionPathCheckedRight

            }
            val directory = File(pathToDel)
            if (directory.deleteRecursively()) {
                showDialogAlert("Moved successfully", "Moved")
            } else {

                showDialogAlert("Moved failed", "Moved")
            }
            reloadListing("both")
        }


        // button create
        val buttonEdit = findViewById<Button>(R.id.editBtn)

        buttonEdit.setOnClickListener {
//            setContentView(R.layout.activity_edit)
            setContentView(R.layout.activity_edit)
            var pathToEdit = ""

            if(lastColumnCheck == "l"){
                pathToEdit = currPathL+"/"+actionPathCheckedLeft

            }
            else {
                pathToEdit = currPathR+"/"+actionPathCheckedRight

            }
            val file = File(pathToEdit)

            if(file.getExtension() == "txt"){
                setContentView(R.layout.activity_edit)

                val reader = BufferedReader(file.reader())
                val editTextarea = findViewById<EditText>(R.id.editTextarea)
                var contentToRender = ""

                var line = reader.readLine()
                while (line != null) {
                    println("linia wirtualna")
                    println(line)

                    contentToRender += line
                    line = reader.readLine()

                }
                editTextarea.setText(contentToRender)


                reader.close()
                val buttonBack = findViewById<Button>(R.id.goToHome)

                buttonBack.setOnClickListener {
                    println("second view")
                    setContentView(R.layout.activity_main)
                    reloadListing("both")
                    recreate()


                }
                val buttonSave = findViewById<Button>(R.id.saveBtn)

                buttonSave.setOnClickListener {
                    println("second view")
                    val currContent = editTextarea.getText()
                    val file = File(pathToEdit)
                    file.writeText(currContent.toString())
                    setContentView(R.layout.activity_main)
                    reloadListing("both")
                    recreate()


                }

            }
            else {
                showDialogAlert("It isn't file", "Edit file")
            }
        }

        // checkers
        // left

        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position

            val item = adapterView.getItemAtPosition(i)

            val directionFolder = item.toString()
            actionPathCheckedLeft = directionFolder
            lastColumnCheck = "l"
        }


        // right

        rListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position
            val item = adapterView.getItemAtPosition(i)

            val directionFolder = item.toString()
            actionPathCheckedRight = directionFolder
            lastColumnCheck = "r"
        }


    }
}


