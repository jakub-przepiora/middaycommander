
package com.example.mid_days_commander

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
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
import kotlin.Any as Any

class CustomAdapter(context: Context, private val list: List<File>) : ArrayAdapter<File>(context, android.R.layout.simple_list_item_1, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView
        val file = list[position]
        if (file.canRead() && !file.canWrite()) { // Set the text color of folders to red
//            textView.setTextColor(Color.RED)
            textView.alpha = 0.2f
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

    fun showDialogAlert(contentAlert: String) {

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

        println(currPathL+"/"+filename+".txt")
        val file = File(currPathL+"/"+filename+".txt")
        file.writeText("Hello, world!")

        // Create the file
        file.createNewFile()
    }
    // fun to open img
    @SuppressLint("MissingInflatedId")
    fun openImg(namefile: String, side: String){
        setContentView(R.layout.activity_image)

        val pathToImg = if(side == "l") currPathL else currPathR

        val imageView = findViewById<ImageView>(R.id.imageViewPresent)

        val image = BitmapFactory.decodeFile(pathToImg+"/"+namefile)

        imageView.setImageBitmap(image)
        val buttonBack = findViewById<Button>(R.id.goToHome)

        buttonBack.setOnClickListener {
            println("second view")
            setContentView(R.layout.activity_main)
            reloadListing("both")
        }

    }

    // Fun to open files
    fun openFile (namefile: String, side: String = "l") {
        val pathToAdd = if(side == "l") currPathL
        else currPathR
        val file = File(pathToAdd+"/"+namefile)
        println(pathToAdd+"/"+namefile)
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
            openImg(namefile, side)
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
//            if (fil.canRead() && !fil.canWrite()) {
//                // The file is only readable
//                filesArrStrCurrently.add(fil.name)
//                fil.alpha = 0.5f
//            }
//            filesArrStrCurrently.add(fil.name)
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
    fun copyFile(from: String, destiny: String, filename: String) {

        // The file that you want to copy
//        val sourceFile = File(from+"/"+filename)
        val sourceFile = File("/sdcard/Documents/tekst1.txt")
        // The destination for the copied file
//        val destinationFile = File(destiny+"/"+filename)
        val destinationFile = File("/sdcard/Download/tekst1.txt")

        // Copy the file
        sourceFile.copyTo(destinationFile, overwrite = true)
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

        val adapter = CustomAdapter(this, filesArrFile)
//        mListView.adapter = arrayAdapter


        mListView.adapter = adapter



        val arrayAdapterRight: ArrayAdapter<*>
        var rListView = findViewById<ListView>(R.id.right)
        arrayAdapterRight = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, filesArrStr)
//        rListView.adapter = arrayAdapterRight
        rListView.adapter = adapter




        // left

        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position
            val item = adapterView.getItemAtPosition(i)

            val directionFolder = item.toString()
            println(directionFolder)
            println(File(currPathL+"/"+directionFolder).isDirectory)
            if(File(currPathL+"/"+directionFolder).isDirectory) {
                mListView.adapter =
                    changeCurrentlyDirectoryOnListView(mListView, directionFolder, "l")
            }
            else {
                openFile(directionFolder, "l")
            }

        }


        // right

        rListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            // Get the object at the clicked position
            val item = adapterView.getItemAtPosition(i)

            val directionFolder = item.toString()
            if(File(currPathL+"/"+directionFolder).isDirectory) {
                rListView.adapter = changeCurrentlyDirectoryOnListView(rListView, directionFolder, "r")
            }
            else {
                openFile(directionFolder, "r")
            }

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
                recreate()

            }
            val alertDialog = builder.create()
            alertDialog.show()

        }
        // button create
        val buttonOpen = findViewById<Button>(R.id.openBtn2)

        buttonOpen.setOnClickListener {
            setContentView(R.layout.activity_edit)
        }

        val buttonCopy = findViewById<Button>(R.id.copyBtn)

        buttonCopy.setOnClickListener{
            copyFile("","","")
        }
    }
}


