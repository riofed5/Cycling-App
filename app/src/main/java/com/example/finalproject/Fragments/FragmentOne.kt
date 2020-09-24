package com.example.finalproject.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_second.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class FragmentOne : Fragment(){

    private var isRunning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button.setOnClickListener(){
            if(!isRunning){
                handleStartBtn()
            }else{
                handleStopBtn()
            }
        }

    }

    private fun handleStartBtn(){
        button.text = "Stop"
        button.setBackgroundColor(resources.getColor(R.color.color_stop_Btn))
        chronometer.start()
        isRunning = true
    }

    private fun handleStopBtn(){
        button.text = "Start"
        button.setBackgroundColor(resources.getColor(R.color.color_start_Btn))
        chronometer.stop()
        isRunning = false
    }
}