package com.example.finalproject.Fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_first.*
import kotlin.math.sqrt

class FragmentOne : Fragment() {

    private lateinit var sensorManager: SensorManager
    private var sensorAccelerometer: Sensor? = null

    private var isRunning: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        start_stop_button.setOnClickListener() {
            val accelerometerSensorListener: SensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor?.type != Sensor.TYPE_LINEAR_ACCELERATION) return
                    var avrgSpeed= 0.0
                    var howMany = 1
                    if (isRunning) {
                        howMany++
                        val magnitude = sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]))
                        avrgSpeed += (magnitude - avrgSpeed) / howMany
                        Log.d("Kqua", "$avrgSpeed")
                        speedTxt.text = magnitude.toString()
                    } else {
                        Log.d("Nhan", "No sensor founded")
                    }
                }
            }

            if (!isRunning) {
                handleStartBtn()
                if (sensorAccelerometer != null) {
                    sensorManager.registerListener(
                        accelerometerSensorListener,
                        sensorAccelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }

            } else {
                handleStopBtn()
            }
        }

        finish_button.setOnClickListener() {
            handleFinishBtn()
        }
    }

    private fun handleStartBtn() {
        start_stop_button.text = "Pause"
        start_stop_button.setBackgroundColor(resources.getColor(R.color.color_stop_Btn))
        finish_button.isEnabled = true
        finish_button.setBackgroundColor(resources.getColor(R.color.color_finish_Btn))
        chronometer.start()
        isRunning = true
    }

    private fun handleStopBtn() {
        isRunning = false
        start_stop_button.text = "Start"
        start_stop_button.setBackgroundColor(resources.getColor(R.color.color_start_Btn))
        chronometer.stop()
    }

    private fun handleFinishBtn() {
        isRunning = false
        chronometer.base = SystemClock.elapsedRealtime();
        chronometer.stop()
        finish_button.isEnabled = false
    }
}