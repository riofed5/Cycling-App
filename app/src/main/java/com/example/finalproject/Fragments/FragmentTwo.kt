package com.example.finalproject.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.finalproject.R
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_second.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory


class FragmentTwo : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = requireActivity().applicationContext
        //important! set your user agent to prevent getting banned
        //from the osm servers
        Configuration.getInstance().load(
            ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx)
        )

//        //inflate layout after loading (to make sure that app can
//        //write to cache)

//        val map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true);
        map.controller.setZoom(9.0)
//
//
//        map.controller.setCenter(GeoPoint(60.208010, 24.662800))
//        val startPoint = GeoPoint(60.208010, 24.662800)
//
//        val startMarker = Marker(map)
//        startMarker.position = startPoint
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//        map.overlays.add(startMarker)
    }
}