package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db by lazy { CyclingDatabase.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graph.gridLabelRenderer.horizontalAxisTitle = "Day"
        graph.gridLabelRenderer.verticalAxisTitle = "Distance (Km)"

        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(150.0)

        start_btn.setOnClickListener {
            val intent = Intent(this, SecondActivy::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            val dataPoints: MutableList<DataPoint> = mutableListOf()
            db.cyclingDataDao().getAll().forEach {
                dataPoints.add(DataPoint(Date(it.date), it.distanceTraveled.toDouble()))
            }
            graph.addSeries(LineGraphSeries(dataPoints.toTypedArray()))
        }
    }
}