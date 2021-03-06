package com.example.finalproject.Fragments

import android.content.Context
import android.content.Intent
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
import com.example.finalproject.DataController.CyclingData
import com.example.finalproject.DataController.CyclingDatabase
import com.example.finalproject.DataController.DateHelper
import com.example.finalproject.DataGraphActivity
import com.example.finalproject.DataRecordActivity
import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class FragmentSensorData : Fragment() {
    private lateinit var sensorManager: SensorManager
    private var sensorAccelerometer: Sensor? = null
    private var isRunning: Boolean = false
    private var isBackToHome: Boolean = false
    private var timeCycling = 0
    private var timeWhenStopped: Long = 0
    private var sensorDataCount: Int = 0
    private var averageSpeed: Double = 0.0
    private var highestSpeed: Double = 0.0
    private val db by lazy { CyclingDatabase.get(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize sensor Manager
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        Log.d("Test", "On Create View: ${timeWhenStopped}")

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Btn Start&PauseFinish handle onClick
        start_pause_button.setOnClickListener {
            //Initialize accelerometer sensor
            val accelerometerSensorListener: SensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor?.type != Sensor.TYPE_LINEAR_ACCELERATION) return
                    if (isRunning) {
                        sensorDataCount++
                        //Get current speed
                        val magnitude =
                            sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]))

                        //Get average Speed to calculate total distance
                        if (magnitude > highestSpeed) {
                            highestSpeed = magnitude.toDouble()
                        }
                        averageSpeed =
                            (averageSpeed * sensorDataCount + magnitude) / (sensorDataCount + 1)

                        //Update current Speed to UI
                        speedTxt.text = "%.2f".format(magnitude)
                    } else {
                        Log.d("Error", "No sensor founded")
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

        //Btn Finish handle onClick
        finish_button.setOnClickListener {
            if (!isBackToHome) {
                handleFinishBtn()
            } else {
                handleBackToHomeBtn()
            }
        }

    }

    private fun handleBackToHomeBtn() {
        val intent = Intent(requireActivity(), DataGraphActivity::class.java)
        requireActivity().startActivity(intent)
    }

    private fun handleStartBtn() {
        isRunning = true

        //Change state button from Start to Pause
        start_pause_button.text = getString(R.string.pause)
        start_pause_button.setBackgroundColor(requireActivity().getColor(R.color.color_stop_Btn))

        //Enable Finish button
        finish_button.isEnabled = true

        //Set up chronometer
        chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
        chronometer.start()

    }

    private fun handleStopBtn() {
        isRunning = false

        //Change state button from Pause to Start
        start_pause_button.text = getString(R.string.start)

        //Set up chronometer
        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()

        //Calculate timeCycling
        timeCycling = calculateElapsedTime()

        //Save data
        saveCyclingData()

    }

    private fun handleFinishBtn() {
        isRunning = false


        start_pause_button.text = getString(R.string.start)

        //Set up chronometer
        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()

        //Calculate timeCycling
        timeCycling = calculateElapsedTime()

        //Set up chronometer
        chronometer.base = SystemClock.elapsedRealtime()
        timeWhenStopped = 0

        //Save data
        saveCyclingData()

        //Change state of finish button
        speedTxt.text = "0"
        finish_button.text = requireActivity().getString(R.string.back_to_main_btn)
        isBackToHome = true

    }

    private fun saveCyclingData() {
        val aveSpeedToSave = "%.2f".format(averageSpeed * 3.6).toFloat()
        val highSpeedToSave = "%.2f".format(highestSpeed * 3.6).toFloat()
        val timeCyclingToSave = timeCycling / 3600f
        val distanceToSave = averageSpeed * 3.6 * timeCyclingToSave
        val currentDate = DateHelper.getCurrentDateResetTime()
        GlobalScope.launch {
            val previousDistance =
                db.cyclingDataDao().getDateData(currentDate.time)?.distanceTraveled ?: 0f
            if (previousDistance == 0f) {
                db.cyclingDataDao().insert(
                    CyclingData(
                        currentDate.time,
                        currentDate.time,
                        aveSpeedToSave,
                        highSpeedToSave,
                        distanceToSave.toFloat()
                    )
                )
                return@launch
            }
            db.cyclingDataDao().update(
                CyclingData(
                    currentDate.time,
                    currentDate.time,
                    aveSpeedToSave,
                    highSpeedToSave,
                    (previousDistance + distanceToSave).toFloat()
                )
            )
        }
    }


    private fun calculateElapsedTime(): Int {
        var stoppedMilliseconds = 0

        //Get Text from chronometer
        val chronoText: String = chronometer.text.toString()
        val array = chronoText.split(":".toRegex()).toTypedArray()

        if (array.size == 2) {
            stoppedMilliseconds = (array[0].toInt() * 60
                    + array[1].toInt())
        } else if (array.size == 3) {
            stoppedMilliseconds =
                array[0].toInt() * 60 * 60 + array[1].toInt() * 60 + array[2].toInt()
        }
        Log.d("Time", "$stoppedMilliseconds sec")
        return stoppedMilliseconds
    }

}