package com.example.finalproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Fragments.FragmentMap
import com.example.finalproject.Fragments.FragmentSensorData
import kotlinx.android.synthetic.main.activity_second_activy.*

class DataRecordActivity : AppCompatActivity() {

    private var isFragmentOneLoaded = true
    private val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_activy)

        showFragmentOne()
        changFrag_btn.setOnClickListener {
            if (isFragmentOneLoaded) {
                showFragmentTwo()
            } else {
                showFragmentOne()
            }
        }
    }

    private fun showFragmentOne() {
        changFrag_btn.text = getString(R.string.display_map)
        val transaction = manager.beginTransaction()
        val fragment = FragmentSensorData()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
        isFragmentOneLoaded = true
    }

    private fun showFragmentTwo() {
        changFrag_btn.text = getString(R.string.display_data)
        val transaction = manager.beginTransaction()
        val fragment = FragmentMap()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
        isFragmentOneLoaded = false
    }

}