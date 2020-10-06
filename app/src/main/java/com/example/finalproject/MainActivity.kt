package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.DataController.CyclingDatabase
import com.example.finalproject.DataController.DateHelper
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db by lazy { CyclingDatabase.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        graph.gridLabelRenderer.horizontalAxisTitle = "Day"
//        graph.gridLabelRenderer.verticalAxisTitle = "Distance (Km)"

        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(20.0)

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        graph.gridLabelRenderer.numHorizontalLabels = 3
        graph.viewport.isXAxisBoundsManual = true
        graph.gridLabelRenderer.setHumanRounding(false)

        start_btn.setOnClickListener {
            startActivity(Intent(this, SecondActivy::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            val dataPoints: MutableList<DataPoint> = mutableListOf()
            val dataList = db.cyclingDataDao().getAll()
            if (dataList.isEmpty()) return@launch

            val lastDateData = dataList[dataList.lastIndex]
            dataList.forEach {
                dataPoints.add(DataPoint(Date(it.date), it.distanceTraveled.toDouble()))
            }

            val barGraphSeries = BarGraphSeries(dataPoints.toTypedArray())
            barGraphSeries.isDrawValuesOnTop = true
            graph.addSeries(barGraphSeries)
            graph.viewport.setMinX(dataList[0].date.toDouble())
            if (lastDateData.date != dataList[0].date) {
                graph.viewport.setMaxX(lastDateData.date.toDouble())
            }

            withContext(Dispatchers.Main) {
                val currentDate = DateHelper.getCurrentDateResetTime().time
                if (currentDate == lastDateData.date) {
                    travelDistanceText.text = "%.2f".format(lastDateData.distanceTraveled)
                    averageSpeedText.text = lastDateData.averageSpeed.toString()
                    highestSpeedText.text = lastDateData.highestSpeed.toString()
                }
            }
        }
    }
}