package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graph.gridLabelRenderer.horizontalAxisTitle = "Day"
        graph.gridLabelRenderer.verticalAxisTitle = "Distance (Km) "

        graph.viewport.setMinY(1.0)
        graph.viewport.setMaxY(150.0)

        start_btn.setOnClickListener() {
            val intent = Intent(this, SecondActivy::class.java)
            startActivity(intent)
        }
    }
}