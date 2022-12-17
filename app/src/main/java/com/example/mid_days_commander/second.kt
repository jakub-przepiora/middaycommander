package com.example.mid_days_commander

import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mid_days_commander.databinding.ActivitySecondBinding

class second : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("SeconD view render")
        setContentView(binding.root)

        val buttonBack = findViewById<Button>(R.id.goToHome)

        buttonBack.setOnClickListener {
            println("second view")
            setContentView(R.layout.activity_main)
        }
    }


}